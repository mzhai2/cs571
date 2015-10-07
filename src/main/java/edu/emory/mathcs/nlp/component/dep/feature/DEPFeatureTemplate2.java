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
package edu.emory.mathcs.nlp.component.dep.feature;

import edu.emory.mathcs.nlp.component.dep.DEPFeatureTemplate;
import edu.emory.mathcs.nlp.component.util.feature.*;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPFeatureTemplate2 extends DEPFeatureTemplate
{
	private static final long serialVersionUID = 4717085054409332081L;

	@Override
	protected void init() {
		//basic
		add(new FeatureItem<>(Source.i, 0, Field.lemma));
		add(new FeatureItem<>(Source.j, 0, Field.lemma));
		add(new FeatureItem<>(Source.i, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.j, 0, Field.pos_tag));

		add(new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.lemma));
		add(new FeatureItem<>(Source.j, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.lemma));

		add(new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.lemma));
		add(new FeatureItem<>(Source.i, 0, Field.lemma), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, 0, Field.lemma), new FeatureItem<>(Source.j, 0, Field.lemma));

		//1 gram
		add(new FeatureItem<>(Source.k, 2, Field.lemma));
		add(new FeatureItem<>(Source.i, -1, Field.pos_tag));
		add(new FeatureItem<>(Source.i, -1, Field.lemma));
		add(new FeatureItem<>(Source.i, 1, Field.lemma));
		add(new FeatureItem<>(Source.j, -2, Field.lemma));
		add(new FeatureItem<>(Source.j, -1, Field.lemma));
		add(new FeatureItem<>(Source.j, 1, Field.lemma));
		add(new FeatureItem<>(Source.j, 2, Field.lemma));
		add(new FeatureItem<>(Source.i, -2, Field.pos_tag));
		add(new FeatureItem<>(Source.i, -1, Field.pos_tag));
		add(new FeatureItem<>(Source.i, 1, Field.pos_tag));
		add(new FeatureItem<>(Source.i, 2, Field.pos_tag));
		add(new FeatureItem<>(Source.j, -1, Field.pos_tag));
		add(new FeatureItem<>(Source.j, 1, Field.pos_tag));

		//2 gram
		add(new FeatureItem<>(Source.j, 1, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.k, 2, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.k, 2, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));

		add(new FeatureItem<>(Source.j, -1, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.lemma));
		add(new FeatureItem<>(Source.j, 1, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.lemma));
		add(new FeatureItem<>(Source.j, 1, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.lemma));
		add(new FeatureItem<>(Source.j, 1, Field.lemma), new FeatureItem<>(Source.i, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.j, 1, Field.lemma), new FeatureItem<>(Source.j, 0, Field.pos_tag));

		add(new FeatureItem<>(Source.i, 1, Field.lemma), new FeatureItem<>(Source.i, 0, Field.lemma));
		add(new FeatureItem<>(Source.i, 1, Field.lemma), new FeatureItem<>(Source.j, 0, Field.lemma));



		//3 gram
		add(new FeatureItem<>(Source.i, -2, Field.pos_tag), new FeatureItem<>(Source.i, -1, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, -1, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.i, 1, Field.pos_tag));
		add(new FeatureItem<>(Source.j, -1, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag), new FeatureItem<>(Source.j, 1, Field.pos_tag));
		add(new FeatureItem<>(Source.j, 0, Field.pos_tag), new FeatureItem<>(Source.j, 1, Field.pos_tag), new FeatureItem<>(Source.j, 2, Field.pos_tag));

		add(new FeatureItem<>(Source.k, 3, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, -1, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, 1, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.j, -2, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.j, -1, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.j, 1, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.j, 2, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.j, 3, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));

		//valency
		add(new FeatureItem<>(Source.i, null, 0, Field.valency, Direction.all), new FeatureItem<>(Source.i, 0, Field.lemma));
		add(new FeatureItem<>(Source.j, null, 0, Field.valency, Direction.all), new FeatureItem<>(Source.j, 0, Field.lemma));

		//2nd order
		add(new FeatureItem<>(Source.i, 0, Field.dependency_label));
		add(new FeatureItem<>(Source.j, 0, Field.dependency_label));
		add(new FeatureItem<>(Source.i, Relation.lmd, 0, Field.dependency_label));

		add(new FeatureItem<>(Source.i, Relation.h, 0, Field.lemma));
		add(new FeatureItem<>(Source.i, Relation.rmd, 0, Field.lemma));
		add(new FeatureItem<>(Source.j, Relation.lmd, 0, Field.lemma));

		add(new FeatureItem<>(Source.i, Relation.h, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, Relation.rmd, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.j, Relation.lmd, 0, Field.pos_tag));

		add(new FeatureItem<>(Source.i, 0, Field.dependency_label), new FeatureItem<>(Source.i, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, 0, Field.dependency_label), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, 0, Field.dependency_label), new FeatureItem<>(Source.i, 0, Field.lemma));
		add(new FeatureItem<>(Source.i, 0, Field.dependency_label), new FeatureItem<>(Source.j, 0, Field.lemma));
		add(new FeatureItem<>(Source.i, Relation.lmd, 0, Field.dependency_label), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, Relation.rmd, 0, Field.dependency_label), new FeatureItem<>(Source.j, 0, Field.pos_tag));

		add(new FeatureItem<>(Source.i, Relation.h, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, Relation.lmd, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, Relation.rmd, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));


		add(new FeatureItem<>(Source.i, 0, Field.dependency_label), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, Relation.lmd, 0, Field.dependency_label), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.j, Relation.lmd, 0, Field.dependency_label), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, Relation.lns, 0, Field.dependency_label), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));

		add(new FeatureItem<>(Source.i, Relation.lmd, 0, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, Relation.rmd, 0, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.j, Relation.lmd, 0, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));

		//3rd order
		add(new FeatureItem<>(Source.i, Relation.h, 0, Field.dependency_label));
		add(new FeatureItem<>(Source.j, Relation.h, 0, Field.dependency_label));
		add(new FeatureItem<>(Source.i, Relation.lmd2, 0, Field.dependency_label));

		add(new FeatureItem<>(Source.i, Relation.h2, 0, Field.lemma));
		add(new FeatureItem<>(Source.i, Relation.rmd2, 0, Field.lemma));
		add(new FeatureItem<>(Source.j, Relation.lmd2, 0, Field.lemma));

		add(new FeatureItem<>(Source.i, Relation.h2, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, Relation.rmd2, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.j, Relation.lmd2, 0, Field.pos_tag));

		add(new FeatureItem<>(Source.i, Relation.h, 0, Field.dependency_label), new FeatureItem<>(Source.i, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, Relation.h, 0, Field.dependency_label), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, Relation.h, 0, Field.dependency_label), new FeatureItem<>(Source.j, 0, Field.lemma));

		add(new FeatureItem<>(Source.i, Relation.h, 0, Field.dependency_label), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, Relation.lmd2, 0, Field.dependency_label), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.j, Relation.lmd2, 0, Field.dependency_label), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, Relation.lns2, 0, Field.dependency_label), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));

		add(new FeatureItem<>(Source.i, Relation.lmd2, 0, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.j, Relation.lmd2, 0, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, Relation.rmd2, 0, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, Relation.lmd2, 0, Field.pos_tag), new FeatureItem<>(Source.i, Relation.lmd, 0, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag));

		//distributional semantics


		// boolean features
		addSet(new FeatureItem<>(Source.i, 0, Field.binary));
		addSet(new FeatureItem<>(Source.j, 0, Field.binary));
	}
}
