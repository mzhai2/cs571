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
public class PerceptronRDA extends SGDClassification
{
    protected final double epsilon = 0.00001;
    protected WeightVector averageGradient;
    protected int count;
    protected double l1;
    protected double gamma; // L/D gives best convergence bound
    protected double enhancer;

    // l1 = lambda 0.01 to 10
    // l1 RDA gamma=5000 rho =0
    //
    // high regularization:
    // enhancer = rho = 0.005
    //


    public PerceptronRDA(WeightVector weightVector, boolean average, double learningRate)
    {
        super(weightVector, average, learningRate);
        averageGradient = weightVector.createEmptyVector();
        count = 1;

        l1 = 0.01;
        enhancer =0;
        gamma = 5000;
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
            updateAverageGradient(yp, yn, x);
            update(yp, yn, x);
        }
        updateRDA();
        count++;
    }

    @Override
    protected double getGradient(int y, int xi)
    {
        return learning_rate;
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

    private void updateAverageGradient(int yp,int yn, Vector x) {
        float term = (count-1)/count;
        for (int i=0;i<averageGradient.toArray().length;i++) {
            averageGradient.toArray()[i] = term*averageGradient.toArray()[i];
        }
        double gp, gn;

        for (IndexValuePair xi : x)
        {
            gp =  getGradient(yp, xi.getIndex()) * xi.getValue()/count;
            gn = -getGradient(yn, xi.getIndex()) * xi.getValue()/count;
            averageGradient.add(yp, xi.getIndex(), gp);
            averageGradient.add(yn, xi.getIndex(), gn);
        }
    }

    protected void updateRDA()
    {
        double RDA = l1 + enhancer/Math.sqrt(count);

        float[] grad = averageGradient.toArray();
        for (int i=0; i<grad.length; i++) {
            double partial = grad[i];
            if (Math.abs(partial) >= RDA)
                weight_vector.toArray()[i] += regularizeGradientRDA(partial, RDA);
        }
    }

    protected void updateElastic(int yp, int yn, Vector x)
    {
        float[] grad = averageGradient.toArray();
        for (int i=0; i<grad.length; i++) {
            double partial = grad[i];
            if (Math.abs(partial) >= l1)
                weight_vector.toArray()[i] += regularizeGradientElastic(partial, l1, 0.01);
        }
    }

    private double regularizeGradientRDA(double gradient, double RDA) {
        return Math.sqrt(count)/gamma*gradient-RDA*Math.signum(gradient);
    }

    private double regularizeGradientElastic(double gradient, double l1, double convexity) {
        return gamma*gradient-l1*Math.signum(gradient)/convexity;
    }
}