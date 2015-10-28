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
package edu.emory.mathcs.nlp.vsm.reader;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import edu.emory.mathcs.nlp.vsm.util.Vocabulary;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public abstract class Reader<N>
{
	static public final int VOCAB_REDUCE_SIZE = 21000000;
	public abstract void add(Vocabulary vocab, N[] nodes);
	public abstract void open(InputStream in);
	public abstract void close();
	public abstract N[]  next();
	
	/**
	 * All words in the training files are first added then sorted by their counts in descending order.
	 * @param minCount words whose counts are less than this are discarded. 
	 * @return the total number of word tokens learned.
	 */
	public long learn(List<String> inputFiles, Vocabulary vocab, int minCount) throws IOException
	{
		N[] nodes;
		
		for (String inputFile : inputFiles)
		{
			open(new BufferedInputStream(new FileInputStream(inputFile)));
			while ((nodes = next()) != null) add(vocab, nodes);
			if (vocab.size() > VOCAB_REDUCE_SIZE) vocab.reduce();
			close();
		}
		
		long count = vocab.sort(minCount);
		return count;
	}
}
