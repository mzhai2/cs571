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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.emory.mathcs.nlp.component.util.eval.Eval;
import edu.emory.mathcs.nlp.component.util.state.NLPState;
import edu.emory.mathcs.nlp.learn.util.StringPrediction;

/**
 * Created by Mike on 9/29/15.
 */
public class DynDEPState<N extends DEPNode> extends NLPState<N>
    {
        static public final String LEFT_ARC  = "LA-";
        static public final String RIGHT_ARC = "RA-";
        static public final String SHIFT     = "S";
        static public final String REDUCE    = "R";

        private DEPArc[]   oracle;
        private IntArrayList stack;
        private int          input;

        public DynDEPState(N[] nodes)
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
            oracle = Arrays.stream(nodes).map(n -> n.clearDependencies()).toArray(DEPArc[]::new);
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


        @Override
        public Set<String> getDynamicOraclePrediction() {
            Set<String> valids = new HashSet<>();

            DEPArc o = oracle[stack.topInt()];

            if (o.isNode(getInput(0)) || !depInBuffer(o))
                valids.add(LEFT_ARC);

            o = oracle[input];

            if (o.isNode(getStack(0)) || (!depInBuffer(o) && (!depInstack(o))))
                valids.add(RIGHT_ARC);

            if (isOracleReduce())
                valids.add(REDUCE);

            if (depInstack(o) || headInStack(o) )
                valids.add(SHIFT);
            return valids;
        }

        private boolean depInBuffer(DEPArc o) {
            for (int i = input; i<nodes.length;i++) {
                N n = nodes[i];
                if (o.isNode(n))
                    return true;
            }
            return false;
        }
        private boolean depInstack(DEPArc o) {
            for (int i=0; i <stack.size(); i++)
                if (o.isNode(getStack(i)))
                    return true;
            return false;
        }

        private boolean headInStack(DEPArc o) {
            for (int i=0; i <stack.size(); i++)
                if (oracle[stack.getInt(i)].isNode(o.getNode()))
                    return true;
            return false;
        }

//	====================================== TRANSITION ======================================

        @Override
        public void next(StringPrediction prediction)
        {
            String label = prediction.getLabel();
//		System.out.println(label+" "+stack.toString()+" "+input);

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

            ((DEPEval)eval).add(las, uas, nodes.length-1);
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

}
