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
package edu.emory.mathcs.nlp.benchmark;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class Benchmark
{
//	@Test
	public void speed()
	{
		Random rand = new Random();
		
		for (int i=0; i<10; i++)
		{
			for (int j=0; j<20; j++)
			{
				System.out.print((int)Math.abs(rand.nextInt())%2);
			}
			
			System.out.println();
			
		}
	}
	
	@Test
	public void test()
	{
		IntSet set = new IntOpenHashSet();
		set.add(1);
		set.add(3);
		set.add(5);
		System.out.println(Arrays.toString(set.toIntArray()));
	}
}
