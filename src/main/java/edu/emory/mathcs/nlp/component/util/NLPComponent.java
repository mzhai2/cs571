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
package edu.emory.mathcs.nlp.component.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;
import java.util.Set;

import edu.emory.mathcs.nlp.component.util.eval.Eval;
import edu.emory.mathcs.nlp.component.util.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.util.state.NLPState;
import edu.emory.mathcs.nlp.learn.model.StringModel;
import edu.emory.mathcs.nlp.learn.optimization.OnlineOptimizer;
import edu.emory.mathcs.nlp.learn.optimization.Optimizer;
import edu.emory.mathcs.nlp.learn.util.Instance;
import edu.emory.mathcs.nlp.learn.util.StringPrediction;
import edu.emory.mathcs.nlp.learn.vector.StringVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class NLPComponent<N,S extends NLPState<N>> implements Serializable
{
	private static final long serialVersionUID = 4546728532759275929L;
	protected FeatureTemplate<N,S> feature_template;
	protected StringModel[] models;
	protected NLPFlag flag;
	protected Eval eval;
	
//	============================== CONSTRUCTORS ==============================
	
	public NLPComponent(StringModel[] models)
	{
		setModels(models);
	}
	
//	============================== SERIALIZATION ==============================
	
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		feature_template = (FeatureTemplate<N,S>)in.readObject();
		models = (StringModel[])in.readObject();
		readLexicons(in);
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeObject(feature_template);
		out.writeObject(models);
		writeLexicons(out);
	}
	
	protected abstract void readLexicons (ObjectInputStream in) throws IOException, ClassNotFoundException;
	protected abstract void writeLexicons(ObjectOutputStream out) throws IOException;
	
//	============================== MODELS ==============================
	
	public StringModel[] getModels()
	{
		return models;
	}
	
	public void setModels(StringModel[] model)
	{
		this.models = model;
	}
	
//	============================== FEATURE ==============================

	public FeatureTemplate<N,S> getFeatureTemplate()
	{
		return feature_template;
	}

	public void setFeatureTemplate(FeatureTemplate<N,S> template)
	{
		feature_template = template;
	}
	
//	============================== FLAG ==============================
	
	public NLPFlag getFlag()
	{
		return flag;
	}
	
	public void setFlag(NLPFlag flag)
	{
		this.flag = flag;
	}
	
	public boolean isTrain()
	{
		return flag == NLPFlag.TRAIN;
	}
	
	public boolean isAggregate()
	{
		return flag == NLPFlag.AGGREGATE;
	}
	
	public boolean isTrainOrAggregate()
	{
		return isTrain() || isAggregate();
	}
	
	public boolean isDecode()
	{
		return flag == NLPFlag.DECODE;
	}
	
	public boolean isEvaluate()
	{
		return flag == NLPFlag.EVALUATE;
	}
	
//	============================== EVALUATOR ==============================

	public Eval getEval()
	{
		return eval;
	}
	
	public void setEval(Eval eval)
	{
		this.eval = eval;
	}
	
//	============================== PROCESS ==============================
	
	/** @return the processing state for the input nodes. */
	protected abstract S createState(N[] nodes);
	/** @return the prediction made by the statistical model(s). */
	protected abstract StringPrediction getModelPrediction(S state, StringVector vector);
	/** Adds a training instance (label, x) to the statistical model. */
	protected abstract void addInstance(String label, StringVector vector);
	protected abstract void addInstance(Set<String> label, StringVector vector);

	public void process(N[] nodes)
	{
		S state = createState(nodes);
		feature_template.setState(state);
		if (!isDecode()) state.saveOracle();
		
		while (!state.isTerminate())
		{
			StringVector vector = extractFeatures();

			if (isTrainOrAggregate()) addInstance(state.getOraclePrediction(), vector);
			StringPrediction label = getPrediction(state, vector);
			state.next(label);
		}
	
		if (isEvaluate()) state.evaluate(eval);
	}
	public void processD(N[] nodes, OnlineOptimizer o, StringModel model, int iter)
	{
		S state = createState(nodes);
		feature_template.setState(state);
		if (!isDecode()) state.saveOracle();

		while (!state.isTerminate())
		{
			StringVector vector = extractFeatures();

			if (isTrainOrAggregate()) {
				Set<String> zeroCosts = state.getDynamicOraclePrediction();
				System.out.println("zero cost oracle transitions: " +zeroCosts.toString());
				Instance instance = model.vectorize(zeroCosts, vector);
				int predicted = o.trainOnline(instance);
				String label = model.unVectorize(chooseNextExplore(iter, predicted, instance.getLabels()));
				StringPrediction prediction = new StringPrediction(label, 1);
				state.next(prediction);

			}
			else {
				StringPrediction prediction = getModelPrediction(state, vector);
				state.next(prediction);
			}
		}

		if (isEvaluate()) state.evaluate(eval);
	}

	private int chooseNextExplore(int iter, int predicted, Set<Integer> zeroCosts) {
		System.out.println(zeroCosts.toString());
		if (iter > 2 && Math.random() > .1)
			return predicted;
		else if (zeroCosts.contains(predicted))
			return predicted;
		else
			return (Integer)zeroCosts.toArray()[new Random().nextInt(zeroCosts.size())];

	}

	/** @return the oracle prediction for training; otherwise, the model predict. */
	protected StringPrediction getPrediction(S state, StringVector vector)
	{
		return isTrain() ? new StringPrediction(state.getOraclePrediction(), 1) : getModelPrediction(state, vector);
	}
	/** @return the vector consisting of all features extracted from the state. */
	protected StringVector extractFeatures()
	{
		return feature_template.extractFeatures();
	}
}
