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
public class AdaGradRDA extends SGDClassification
{
    protected final float epsilon = 0.00001f;
    protected WeightVector diagonals;
    protected WeightVector sumGradients;
    protected int count;
    protected float l1;
    protected float gamma;
    protected float enhancer;

    // l1 = lambda 0.01 to 10
    // l1 RDA gamma=5000 rho =0
    //
    // high regularization:
    // enhancer = rho = 0.005
    //


    public AdaGradRDA(WeightVector weightVector, boolean average, float learningRate)
    {
        super(weightVector, average, learningRate);
        diagonals = weightVector.createEmptyVector();
        sumGradients = weightVector.createEmptyVector();
        count = 1;

        l1 = 0.00001f;
        enhancer =0;
        gamma = 1000;
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
            updateDiagonals(yp, x);
            updateDiagonals(yn, x);
            updateSumGradients(yp, yn, x);
            updateRDA();
        }
        if (count %1000 == 0)
        System.out.println(count);
        count++;
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
        join.add("l1 = " + l1);
        join.add("enhancer= " + enhancer);
        join.add("gamma = " + gamma);

        return "AdaGradRDA: "+join.toString();
    }

    private void updateSumGradients(int yp,int yn, Vector x) {
        double gp, gn;

        for (IndexValuePair xi : x)
        {
            gp =  getGradient(yp, xi.getIndex()) * xi.getValue();
            gn = -getGradient(yn, xi.getIndex()) * xi.getValue();
            sumGradients.add(yp, xi.getIndex(), gp);
            sumGradients.add(yn, xi.getIndex(), gn);
        }
    }

    protected void updateRDA()
    {
        float RDA = l1;
//        + enhancer/(float)Math.sqrt(count);

        float[] grad = sumGradients.toArray();
        for (int i=0; i<grad.length; i++) {
            if (grad[i] < 0.00001)
                continue;
            float mod = Math.abs(grad[i])/count - RDA;
//            System.out.println(mod);
            if (mod < 0)
                continue;
            if (mod == 0) {
                weight_vector.toArray()[i] = 0;
            }
            else {
                weight_vector.toArray()[i] = signum(grad[i]) * learning_rate * count / diagonals.toArray()[i] * mod;
            }
        }
    }

    private float signum(float f) {
        if (f<0)
            return -1;
        else if (f==0)
            return 0;
        else
            return 1;

    }
//    protected void updateElastic(int yp, int yn, Vector x)
//    {
//        float[] grad = averageGradient.toArray();
//        for (int i=0; i<grad.length; i++) {
//            double partial = grad[i];
//            if (Math.abs(partial) >= l1)
//                weight_vector.toArray()[i] += regularizeGradientElastic(partial, l1, 0.01);
//        }
//    }
//
//    private double regularizeGradientRDA(double partial, double RDA) {
//        return Math.sqrt(count)/gamma*partial-RDA*Math.signum(partial);
//    }
//
//    private double regularizeGradientElastic(double partial, double l1, double convexity) {
//        return gamma*partial-l1*Math.signum(partial)/convexity;
//    }

    private double regularizeGradientRDA(double partial, double RDA) {
        return Math.signum(-partial) * count * partial - Math.max(0, RDA * Math.abs(partial));
    }
}