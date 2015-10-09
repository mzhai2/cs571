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

import java.util.Random;
import java.util.StringJoiner;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class AdaGradTrunc extends SGDClassification
{
	protected final double epsilon = 0.00001;
	protected WeightVector diagonals;
	protected WeightVector possiblePenalty;
	protected WeightVector penaltySum;
	protected double l1 = 0.00001;


	public AdaGradTrunc(WeightVector weightVector, boolean average, float learningRate, double l1)
	{
		super(weightVector, average, learningRate);
		diagonals = weightVector.createEmptyVector();
		possiblePenalty = weightVector.createEmptyVector();
		penaltySum = weightVector.createEmptyVector();
		this.l1 = l1;
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
		int labelSize = weight_vector.labelSize();
		if (yp != yn)
		{
			updateDiagonals(yp, x);
			updateDiagonals(yn, x);
			for (IndexValuePair p : x) {
				possiblePenalty.toArray()[labelSize*p.getIndex() + yp] += l1*getGradient(yp, p.getIndex());
				possiblePenalty.toArray()[labelSize*p.getIndex() + yn] += l1*getGradient(yn, p.getIndex());
				double gp = getGradient(yp, p.getIndex()) * p.getValue();
				double gn = -getGradient(yn, p.getIndex()) * p.getValue();
				weight_vector.add(yp, p.getIndex(), gp);
				weight_vector.add(yn, p.getIndex(), gn);
				applyPenalty(labelSize*p.getIndex()+yp);
				applyPenalty(labelSize*p.getIndex()+yn);
			}
		}
	}

	private void updateDiagonals(int y, Vector x)
	{
		for (IndexValuePair p : x)
			diagonals.add(y, p.getIndex(), MathUtils.sq(p.getValue()));
	}

	@Override
	protected float getGradient(int y, int xi)
	{
		return (float) (learning_rate / (epsilon + Math.sqrt(diagonals.get(y, xi))));
	}

	@Override
	public String toString()
	{
		StringJoiner join = new StringJoiner(", ");

		join.add("average = "+isAveraged());
		join.add("learning rate = "+learning_rate);

		return "AdaGradTrunc: "+join.toString();
	}

	private void applyPenalty(int index) {
		float z = weight_vector.toArray()[index];
		float qi = penaltySum.toArray()[index];
		float u = possiblePenalty.toArray()[index];
		if (z > 0)
			weight_vector.toArray()[index] = Math.max(0, z-(u+qi));
		else
			weight_vector.toArray()[index] = Math.min(0, z+(u-qi));
		penaltySum.toArray()[index] += weight_vector.toArray()[index]-z;
	}
}
