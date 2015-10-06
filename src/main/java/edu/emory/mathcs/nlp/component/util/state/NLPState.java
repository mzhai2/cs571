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
package edu.emory.mathcs.nlp.component.util.state;

import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.component.util.eval.Eval;
import edu.emory.mathcs.nlp.learn.util.StringPrediction;

import java.util.Set;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class NLPState<N>
{
	protected N[] nodes;

	public NLPState(N[] nodes)
	{
		this.nodes = nodes;
	}
	
	/** Clears and saves the gold-standard labels in the input nodes if available. */
	public abstract void saveOracle();
	
	/** @return the oracle prediction of the current state. */
	public abstract String getOraclePrediction();

	public abstract Set<String> getDynamicOraclePrediction();

	/** Applies the prediction and moves onto the next state */
	public abstract void next(StringPrediction prediction);
	
	/** @return true if no more state can be processed; otherwise, false. */
	public abstract boolean isTerminate();
	
	/** Evaluates all predictions given the current input and the evaluator. */
	public abstract void evaluate(Eval eval);

	/** @return the node in the (index+window) position of {@link #nodes} if exists; otherwise, null. */
	public N getNode(int index, int window)
	{
		index += window;
		return DSUtils.isRange(nodes, index) ? nodes[index] : null;
	}
}
