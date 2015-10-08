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
package edu.emory.mathcs.nlp.component.dep;

import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.*;
import java.util.stream.Collectors;

import edu.emory.mathcs.nlp.component.util.eval.Eval;
import edu.emory.mathcs.nlp.component.util.state.NLPState;
import edu.emory.mathcs.nlp.learn.util.StringPrediction;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPState<N extends DEPNode> extends NLPState<N>
{
	static public final String LEFT_ARC  = "LA-";
	static public final String RIGHT_ARC = "RA-";
	static public final String SHIFT     = "S";
	static public final String REDUCE    = "R";
	
	private DEPArc[]     oracle;
	private IntArrayList stack;
	private int          input;
	
	public DEPState(N[] nodes)
	{
		super(nodes);
		stack = new IntArrayList();
		input = 0;
		shift();
	}
	
//	====================================== ORACLE ======================================

	@Override
	public void saveOracle()
	{
		oracle = new DEPArc[nodes.length];

		for (int i=0; i<nodes.length; i++)
		{
			oracle[i] = nodes[i].clearDependencies();
		}
//		oracle = Arrays.stream(nodes).map(n -> n.clearDependencies()).toArray(DEPArc[]::new);
	}

	@Override
	public String getOraclePrediction()
	{
		DEPArc o = oracle[stack.topInt()];

		if (o.isLabel("ARTROOT"))
			return SHIFT;
		// left-arc: input is the head of stack
		if (o.isNode(getInput(0)))
			return LEFT_ARC + o.getLabel();
		
		// right-arc: stack is the head of input
		o = oracle[input];
		if (o.isLabel("ARTROOT"))
			return SHIFT;

		if (o.isNode(getStack(0)))
			return RIGHT_ARC + o.getLabel();
		
		// reduce: stack has the head
		if (isOracleReduce())
			return REDUCE;
		
		return SHIFT;
	}

	private boolean isOracleReduce()
	{
		DEPNode stack = getStack(0);
		if (!stack.hasHead()) return false;
		
		for (int i=input+1; i<nodes.length; i++)
		{
			if (oracle[i].isNode(stack))
				return false;
		}
		
		return true;
	}

//	================================ DYNAMIC ORACLE ======================================


//	@Override
//	public Set<String> getDynamicOraclePrediction() {
//		Set<String> valids = new HashSet<>();
//
//		DEPArc o = oracle[stack.topInt()];
//
//		if ((o.isNode(getInput(0)) ||
//				(!headInBuffer(nodes[stack.topInt()]) && isOracleReduce()) ||
//				(!o.isNode(getInput(0)) && !headInBuffer(nodes[stack.topInt()])))
//						&& (input != nodes.length && input != stack.size()))
//			valids.add(LEFT_ARC);
//
//		o = oracle[input];
//
//		DEPNode currWord = nodes[input];
//		if ((o.isNode(getStack(0)) ||
//				(!headInBuffer(currWord) && !headInStack(currWord) && !depInStack(currWord)) ||
//				(!o.isNode(getStack(0)) && !headInStack(currWord) && !headInBuffer(currWord) && !depInStack(currWord)))
//						&& (input != nodes.length && input != stack.size()))
//			valids.add(RIGHT_ARC);
//
//		if (isOracleReduce() && (input != nodes.length && input != stack.size()))	//no dep in B
//			valids.add(REDUCE);
//
//		if (!headInStack(currWord) && !depInStack(currWord) && (nodes.length - input) > 1)
//			valids.add(SHIFT);
//		return valids;
//	}
	@Override
	public Set<String> getDynamicOraclePrediction() {
		Set<String> legal = getLegal();
		Set<String> valids = new HashSet<>();

		DEPArc o = oracle[stack.topInt()];
		String label = o.getLabel();

		if (legal.contains(LEFT_ARC) && zeroCostLeft()) {
			valids.add(LEFT_ARC + o.getLabel());
		}

		o = oracle[input];

		if (legal.contains(RIGHT_ARC) && zeroCostRight()) {
			valids.add(RIGHT_ARC + o.getLabel());
		}
		if (legal.contains(SHIFT) && zeroCostShift()) {
			valids.add(SHIFT);
		}
		if (legal.contains(REDUCE) && zeroCostReduce()) {
			valids.add(REDUCE);
		}

		return valids;
	}

	private boolean zeroCostShift() {
		if (nodes.length-input <= 1)
			return false;
		for (int s : stack)
			if (getHeadID(s) == input || getHeadID(input) == s)
				return false;
		return true;
	}

	private boolean zeroCostReduce() {
		if (stack.size()==0 || nodes.length-input==0)
			return false;

		int s = stack.topInt();
		for (int bi=input;bi<nodes.length;bi++) {
			if (isHead(bi) && isHeadOf(bi, s))
				return false;
		}
		return true;
	}
	private boolean zeroCostLeft() {
		if (stack.size() == 0 || nodes.length-input == 0)
			return false;

		int s = stack.topInt();
		for (int bi = input; bi<nodes.length; bi++) {
			if (isHead(bi) && isHeadOf(bi, s))
				return false;
			if (input != bi && isHeadOf(s, bi))
				return false;
		}
		return true;
	}

	private boolean zeroCostRight() {
		if (stack.size() == 0 || nodes.length-input == 0)
			return false;

		int s = stack.topInt();
		int k;
		if (isHead(input))
			k = getHeadID(input);
		else
			k = -1;

		if (k == s)
			return true;

		boolean k_b_costs = false;
		if (stackContains(k) || bufferContains(k))
			k_b_costs = true;

		Map<Integer, Integer> k_heads = new HashMap<>();
		for (int i=1; i<oracle.length; i++)
			k_heads.put(i, oracle[i].getNode().getID());

		Set<Integer> b_deps = new HashSet<>();
		for (int i=1;i<oracle.length;i++)
			if (oracle[i].getNode().getID() == input)
				b_deps.add(i);

		Set<Integer> b_k_in_stack = b_deps.stream().filter(x -> stack.contains(x)).collect(Collectors.toSet());
		Set<Integer> b_k_final = b_k_in_stack.stream().filter(x -> !k_heads.containsKey(x)).collect(Collectors.toSet());

		if (!bufferContains(k) && !stackContains(k) && b_k_in_stack.size() ==0)
			return true;

		if (k_b_costs)
			return false;

		return b_k_final.size() == 0;
	}

	public boolean stackContains(int i) {
		for (int j=0; j<stack.size();j++)
			if (stack.get(j) == i)
				return true;
		return false;
	}

	public boolean bufferContains(int i) {
		for (int j=input; j<nodes.length;j++)
			if (nodes[j].getID() == i)
				return true;
		return false;
	}
	public int getHeadID(int i) {
		if (oracle[i].getNode() == null)
			return -1;
		return oracle[i].getNode().getID();
	}
	public boolean isHead(int bi) {
		for (int i = 1; i < oracle.length; i++) {
			if (bi == oracle[i].getNode().getID()) {
				return true;
			}
		}
		return false;
	}

	public boolean isHeadOf(int i, int j) {
		if (i==0 && j!=0)
			return oracle[j].getNode().getID() == 0;
		return oracle[i].getNode().getID() == j;
	}

	public Set<String> getLegal() {
		Set<String> legal = new HashSet<>();

		String[] transitions = {LEFT_ARC, RIGHT_ARC, SHIFT, REDUCE};

		boolean left_ok = true;
		boolean right_ok = true;
		boolean shift_ok = true;
		boolean reduce_ok = true;
		if (nodes.length -input==1) {
			right_ok = false;
			shift_ok = false;
		}
		if (stack.size() == 0) {
			left_ok = false;
			right_ok = false;
			reduce_ok = false;
		}
		else {
			int s = stack.topInt();
			boolean alreadyDependent = false;
			for (int i=1;i<input;i++) {
				if (oracle[i].isNode(oracle[s].getNode()))
					alreadyDependent = true;
			}
			if (alreadyDependent)
				left_ok = false;
			else
				reduce_ok = false;
		}
		Boolean[] ok = {left_ok, right_ok,shift_ok, reduce_ok};
		for (int i=0;i<ok.length;i++) {
			if (ok[i] == true) {
				legal.add(transitions[i]);
			}
		}
		return legal;
	}
//
//	private boolean headInBuffer(DEPNode currWord) {
//		for (int i = input+1; i<nodes.length;i++) {
//			if (currWord == oracle[i].getNode())
//				return true;
//		}
//		return false;
//	}
//	private boolean depInStack(DEPNode currWord) {
//		for (int i=0; i <stack.size(); i++)
//			if (oracle[stack.get(i)].isNode(currWord))
//				return true;
//		return false;
//	}
//
//	private boolean headInStack(DEPNode currWord) {	//is this head of anything
//		for (int i=0; i <stack.size(); i++)
//			if(oracle[currWord.getID()].getNode() == nodes[stack.get(i)])
//				return true;
//		return false;
//	}


//	====================================== TRANSITION ======================================
	@Override
	public void next(StringPrediction prediction)
	{
		String label = prediction.getLabel();

		if (label.startsWith(LEFT_ARC))
		{
			DEPNode s = getStack(0);
			DEPNode i = getInput(0);
			
			if (s != nodes[0] && !i.isDescendantOf(s))
			{
				s.setHead(i, label.substring(3));
				label = REDUCE;
			}
			else
				label = SHIFT;
		}
		else if (label.startsWith(RIGHT_ARC))
		{
			DEPNode s = getStack(0);
			DEPNode i = getInput(0);
			
			if (!s.isDescendantOf(i))
				i.setHead(s, label.substring(3));

			label = SHIFT;
		}
		else if (label.equals(REDUCE))
		{
			if (stack.size() == 1)
				label = SHIFT;
		}

		
		switch (label)
		{
		case SHIFT : shift();  break;
		case REDUCE: reduce(); break;
		}
	}
	
	public void shift()
	{
		stack.add(input++);
	}
	
	public void reduce()
	{
		stack.pop();
	}
	
	@Override
	public boolean isTerminate()
	{
		return input >= nodes.length;
	}
	
	/**
	 * @return the window'th top of the stack if exists; otherwise, -1.
	 * @param window 0: top, 1: 2nd-top, so one.
	 */
	public N peekStack(int window)
	{
		return window < stack.size() ? nodes[stack.peekInt(window)] : null;
	}
	
	public N getStack(int window)
	{
		return getNode(stack.topInt(), window);
	}
	
	public N getInput(int window)
	{
		return getNode(input, window);
	}
	
//	====================================== EVALUATE ======================================

	@Override
	public void evaluate(Eval eval)
	{
		int las = 0, uas = 0;
		DEPNode node;
		DEPArc  gold;
		
		for (int i=1; i<nodes.length; i++)
		{
			node = nodes [i];
			gold = oracle[i];

			if (gold.isNode(node.getHead()))
			{
				uas++;
				if (gold.isLabel(node.getLabel())) las++;
			}
		}

		((DEPEval)eval).add(las, uas, nodes.length - 1);
	}

//	============================== UTILITIES ==============================
	
	public boolean isFirst(N node)
	{
		return nodes[1] == node;
	}
	
	public boolean isLast(N node)
	{
		return nodes[nodes.length-1] == node;
	}


}
