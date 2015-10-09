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
package edu.emory.mathcs.nlp.learn.optimization.sgd;

import edu.emory.mathcs.nlp.common.util.MathUtils;
import edu.emory.mathcs.nlp.learn.util.Instance;
import edu.emory.mathcs.nlp.learn.vector.IndexValuePair;
import edu.emory.mathcs.nlp.learn.vector.Vector;
import edu.emory.mathcs.nlp.learn.weight.WeightVector;

import java.util.StringJoiner;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AdaDelta extends SGDClassification
{
	protected final double epsilon = 0.00001;
	protected WeightVector diagonals;
	protected WeightVector gradient;
	protected double decayingRate;
	protected double growthRate;
	protected int window;
	protected int counter;

	public AdaDelta(WeightVector weightVector, boolean average, double learningRate, double decayingRate, int window)
	{
		super(weightVector, average, learningRate);
		diagonals = weightVector.createEmptyVector();
		this.decayingRate = decayingRate;
		this.gradient = weightVector.createEmptyVector();
		this.window = window;
		growthRate = 1-decayingRate;
		counter = 0;
	}

	@Override
	protected void updateBinomial(Instance instance)
	{
	}

	@Override
	protected void updateMultinomial(Instance instance)
	{
		Vector x = instance.getVector();
		int   yp = instance.getLabel();
		int   yn = multinomialBestHingeLoss(instance);
		
		if (yp != yn)
		{
			updateGradient(yp, yn, x);
			updateWeightVector(yp, yn, x);
		}
		if (counter++ % window == 0) {
			decayDiagonal();
			gradient.fill(0);
		}
	}

	private void updateWeightVector(int yp, int yn, Vector x)
	{
		for (IndexValuePair xi : x) {
			weight_vector.add(yp,xi.getIndex(),getAdjustedLearningRate(yp,xi.getIndex()));
			weight_vector.add(yn,xi.getIndex(),-getAdjustedLearningRate(yn,xi.getIndex()));
		}
	}

	private void decayDiagonal()
	{
		float[] d = diagonals.toArray();
		float[] g = gradient.toArray();

		for (int i = 0; i < d.length; i++)
			d[i] = decayWeight(d[i], g[i]);
	}

	protected void updateGradient(int yp, int yn, Vector x)
	{
		for (IndexValuePair xi : x)
		{
			gradient.add(yp, xi.getIndex(), xi.getValue());
			gradient.add(yn, xi.getIndex(), -xi.getValue());
		}
	}
	protected float decayWeight(float previousDiagonal, float gradient)
	{
		return (float)(decayingRate*previousDiagonal + growthRate*MathUtils.sq(gradient));
	}

	@Override
	protected double getAdjustedLearningRate(int y, int xi)
	{
		return learning_rate / (epsilon + Math.sqrt(diagonals.get(y, xi)));
	}
	
	@Override
	public String toString()
	{
		StringJoiner join = new StringJoiner(", ");
		
		join.add("average = "+isAveraged());
		join.add("learning rate = "+learning_rate);
		join.add("decaying rate = "+decayingRate);
		join.add("window " +window);
		return "AdaDelta: "+join.toString();
	}
}
