/**
 * Copyright 2015, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.mathcs.nlp.component.util.train;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import edu.emory.mathcs.nlp.learn.weight.WeightVector;
import org.kohsuke.args4j.Option;

import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.FileUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.util.NLPComponent;
import edu.emory.mathcs.nlp.component.util.NLPFlag;
import edu.emory.mathcs.nlp.component.util.config.NLPConfig;
import edu.emory.mathcs.nlp.component.util.eval.Eval;
import edu.emory.mathcs.nlp.component.util.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.util.reader.TSVReader;
import edu.emory.mathcs.nlp.component.util.state.NLPState;
import edu.emory.mathcs.nlp.learn.model.StringModel;
import edu.emory.mathcs.nlp.learn.optimization.Optimizer;
import edu.emory.mathcs.nlp.learn.optimization.OptimizerType;

/**
 * Provide instances and methods for training NLP components.
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class NLPTrain<N,S extends NLPState<N>>
{
	@Option(name="-c", usage="confinguration file (required)", required=true, metaVar="<filename>")
	public String configuration_file;
	@Option(name="-t", usage="training path (required)", required=true, metaVar="<filepath>")
	public String train_path;
	@Option(name="-te", usage="training file extension (default: *)", required=false, metaVar="<string>")
	public String train_ext = "*";
	@Option(name="-d", usage="development path (required)", required=true, metaVar="<filepath>")
	public String develop_path;
	@Option(name="-de", usage="development file extension (default: *)", required=false, metaVar="<string>")
	public String develop_ext = "*";
	@Option(name="-f", usage="feature template ID (default: 0)", required=false, metaVar="integer")
	public int feature_template = 0;
	@Option(name="-m", usage="model file (optional)", required=false, metaVar="<filename>")
	public String model_file = null;
	
	public NLPTrain() {};
	
	public NLPTrain(String[] args)
	{
		BinUtils.initArgs(args, this);
	}
	
	/** Collects necessary lexicons for the component before training. */
	public abstract void collect(TSVReader<N> reader, List<String> inputFiles, NLPComponent<N,S> component, NLPConfig<N> configuration);
	protected abstract NLPConfig<N> createConfiguration(String filename);
	protected abstract FeatureTemplate<N,S> createFeatureTemplate();
	protected abstract NLPComponent<N,S> createComponent();
	protected abstract Eval createEvaluator();
	
	public void train()
	{
		List<String>      trainFiles    = FileUtils.getFileList(train_path, train_ext);
		List<String>      developFiles  = FileUtils.getFileList(develop_path, develop_ext);
		NLPConfig<N>      configuration = createConfiguration(configuration_file);
		TSVReader<N>      reader        = configuration.getTSVReader();
		NLPComponent<N,S> component     = createComponent();
		
		component.setFeatureTemplate(createFeatureTemplate());
		component.setEval(createEvaluator());

		train(reader, trainFiles, developFiles, configuration, component);
		if (model_file != null) save(component);
	}
	
	public void train(TSVReader<N> reader, List<String> trainFiles, List<String> developFiles, NLPConfig<N> configuration, NLPComponent<N,S> component)
	{
		BinUtils.LOG.info("Collecting lexicons:\n");
		collect(reader, trainFiles, component, configuration);

		Aggregation dagger = configuration.getAggregation();
		StringModel[] models = component.getModels();
		int i, size = models.length, bestIter = 0;
		float[][] bestWeight = new float[size][];
		double prevScore, currScore = -1, bestScore = -1;
		
		for (int iter=0; ; iter++)
		{
			BinUtils.LOG.info(String.format("\nTraining: %d\n\n", iter));
			component.setFlag(iter == 0 ? NLPFlag.TRAIN : NLPFlag.AGGREGATE);
			prevScore = currScore;
			iterate(reader, trainFiles, nodes -> component.process(nodes));
			Optimizer[] optimizers = configuration.getOptimizers(models);
			currScore = trainIter(reader, trainFiles, developFiles, component, configuration, iter, optimizers);
			Set<Integer> removedFeatures = models[0].getRemovedFeatures();
			if (dagger == null) break;	// no aggregating
			
			if (prevScore > currScore + dagger.getToleranceDelta() || iter - dagger.getMaxTolerance() > bestIter)
			{
				for (i=0; i<size; i++) models[i].getWeightVector().fromArray(bestWeight[i]);
				prevScore = currScore;
				break;
			}
			else if (bestScore < currScore)
			{
				for (i=0; i<size; i++) bestWeight[i] = models[i].getWeightVector().toArray().clone();
				bestScore = currScore;
				bestIter  = iter;
			}

			float[] w = component.getModels()[0].getWeightVector().toArray();
			int labelSize = component.getModels()[0].getWeightVector().labelSize();
//			for (int f=0; f<w.length; f++) {
//				if (w[f] == 0) {
//					removedFeatures.add(f);
//				}
//			}
			double threshold = 0.000001;
			for (int k=0; k<w.length; k+=labelSize) {
				float sum = 0;
				for (int j = 0; j < labelSize; j++)
					sum += w[k + j];
				if (sum < threshold) {
					removedFeatures.add(k / labelSize);
					for (int j = 0; j < labelSize; j++)
						w[k + j] = 0;
				}
			}
			
//			float[] newWeights = new float[w.length-removedFeatures.size()*labelSize];
//			int nWcounter = 0;
//			for (int k=0; k<w.length; k+=labelSize) {
//				if (!removedFeatures.contains(k)) {
//					for (int j = 0; j < labelSize; j++) {
//						System.out.println(newWeights[nWcounter + j]);
//						System.out.println(w[k + j]);
//						newWeights[nWcounter + j] = w[k + j];
//					}
//					nWcounter += labelSize;
//				}
//			}
//			component.getModels()[0].getWeightVector().fromArray(newWeights);
//			removedFeatures.clear();
		}
		
		BinUtils.LOG.info(String.format("\nFinal score: %5.2f\n", bestScore));
	}
	
	public double trainIter(TSVReader<N> reader, List<String> trainFiles, List<String> developFiles, NLPComponent<N, ?> component, NLPConfig<N> configuration, int iter, Optimizer[] optimizers)
	{
		StringModel[] models = component.getModels();
		double score = 0;

		for (int i=0; i<optimizers.length; i++)
		{
			Set<Integer> removedFeatures = models[0].getRemovedFeatures();
			if (removedFeatures.size() > 0)
				iterate(reader, trainFiles, nodes -> component.process(nodes, iter, removedFeatures));

			BinUtils.LOG.info(optimizers[i].toString()+", bias = "+models[i].getBias()+"\n");
			BinUtils.LOG.info(models[i].trainInfo()+"\n");
			component.setFlag(NLPFlag.EVALUATE);
			if (optimizers[i].getType() == OptimizerType.ONLINE)
				score = trainOnline(reader, developFiles, component, optimizers[i], models[i], iter);
			else
				score = trainOneVsAll(reader, developFiles, component, optimizers[i], models[i], iter);
		}

		return score;
	}
	
	protected double trainOnline(TSVReader<N> reader, List<String> developFiles, NLPComponent<N,?> component, Optimizer optimizer, StringModel model, int iter)
	{
		Eval eval = component.getEval();
		double prevScore = 0, currScore, bestScore;
		float[] bestWeight = null;
		
		for (int epoch=1; ;epoch++)
		{
			eval.clear();
			optimizer.train(model.getInstanceList());
			iterate(reader, developFiles, nodes -> component.process(nodes, iter, model.getRemovedFeatures()));
			currScore = eval.score();
			bestScore = currScore;

			if (currScore > bestScore) {
				bestWeight = model.getWeightVector().toArray().clone();
				epoch--;
			}
			else if (prevScore < currScore || epoch > 2) {

			}
			else
			{
				model.getWeightVector().fromArray(bestWeight);
				break;
			}
			
			BinUtils.LOG.info(String.format("%3d: %5.2f\n", epoch, currScore));
		}

		return prevScore; 
	}

	protected double trainOneVsAll(TSVReader<N> reader, List<String> developFiles, NLPComponent<N,?> component, Optimizer optimizer, StringModel model, int iter)
	{
		Eval eval = component.getEval();

		eval.clear();
		optimizer.train(model.getInstanceList());
		iterate(reader, developFiles, nodes -> component.process(nodes, iter, model.getRemovedFeatures()));
		BinUtils.LOG.info(String.format("- %s\n", eval.toString()));
		return eval.score();
	}
	
//	=================================== HELPERS ===================================
	
	protected void iterate(TSVReader<N> reader, List<String> inputFiles, Consumer<N[]> f)
	{
		N[] nodes;
		
		for (String inputFile : inputFiles)
		{
			reader.open(IOUtils.createFileInputStream(inputFile));
			
			try
			{
				while ((nodes = reader.next()) != null)
					f.accept(nodes);
			}
			catch (IOException e) {e.printStackTrace();}
		}
	}
	
	public void save(NLPComponent<N,S> component)
	{
		ObjectOutputStream out = IOUtils.createObjectXZBufferedOutputStream(model_file);
		
		try
		{
			out.writeObject(component);
			out.close();
		}
		catch (IOException e) {e.printStackTrace();}
	}
}
