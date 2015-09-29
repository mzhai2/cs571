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
import edu.emory.mathcs.nlp.component.util.feature.Direction;
import edu.emory.mathcs.nlp.component.util.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.util.feature.Field;
import edu.emory.mathcs.nlp.component.util.feature.Relation;
import edu.emory.mathcs.nlp.component.util.feature.Source;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPFeatureTemplate0 extends DEPFeatureTemplate
{
	private static final long serialVersionUID = 4717085054409332081L;

	@Override
	protected void init() {
		// lemma features 
		add(new FeatureItem<>(Source.i, -1, Field.lemma));
		add(new FeatureItem<>(Source.i, 0, Field.lemma));
		add(new FeatureItem<>(Source.i, 1, Field.lemma));

		add(new FeatureItem<>(Source.j, -2, Field.lemma));
		add(new FeatureItem<>(Source.j, -1, Field.lemma));
		add(new FeatureItem<>(Source.j, 0, Field.lemma));
		add(new FeatureItem<>(Source.j, 1, Field.lemma));
		add(new FeatureItem<>(Source.j, 2, Field.lemma));

		add(new FeatureItem<>(Source.k, 1, Field.lemma));

		// pos features
		add(new FeatureItem<>(Source.i, -2, Field.pos_tag));
		add(new FeatureItem<>(Source.i, -1, Field.pos_tag));
		add(new FeatureItem<>(Source.i, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, 1, Field.pos_tag));
		add(new FeatureItem<>(Source.i, 2, Field.pos_tag));

		add(new FeatureItem<>(Source.j, -2, Field.pos_tag));
		add(new FeatureItem<>(Source.j, -1, Field.pos_tag));
		add(new FeatureItem<>(Source.j, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.j, 1, Field.pos_tag));
		add(new FeatureItem<>(Source.j, 2, Field.pos_tag));

		add(new FeatureItem<>(Source.k, 1, Field.pos_tag));
		add(new FeatureItem<>(Source.k, 2, Field.pos_tag));

		// valency features
		add(new FeatureItem<>(Source.i, 0, Field.valency, Direction.all));
		add(new FeatureItem<>(Source.j, 0, Field.valency, Direction.all));

		// 2nd-order features
		add(new FeatureItem<>(Source.i, Relation.h, 0, Field.lemma));
		add(new FeatureItem<>(Source.i, Relation.lmd, 0, Field.lemma));
		add(new FeatureItem<>(Source.i, Relation.rmd, 0, Field.lemma));
		add(new FeatureItem<>(Source.j, Relation.lmd, 0, Field.lemma));

		add(new FeatureItem<>(Source.i, Relation.h, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, Relation.lmd, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, Relation.rmd, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.j, Relation.lmd, 0, Field.pos_tag));

		add(new FeatureItem<>(Source.i, 0, Field.dependency_label));
		add(new FeatureItem<>(Source.i, Relation.lns, 0, Field.dependency_label));
		add(new FeatureItem<>(Source.i, Relation.lmd, 0, Field.dependency_label));
		add(new FeatureItem<>(Source.i, Relation.rmd, 0, Field.dependency_label));
		add(new FeatureItem<>(Source.j, Relation.lmd, 0, Field.dependency_label));

		// 3rd-order features
		add(new FeatureItem<>(Source.i, Relation.h2, 0, Field.lemma));
		add(new FeatureItem<>(Source.i, Relation.lmd2, 0, Field.lemma));
		add(new FeatureItem<>(Source.i, Relation.rmd2, 0, Field.lemma));
		add(new FeatureItem<>(Source.j, Relation.lmd2, 0, Field.lemma));

		add(new FeatureItem<>(Source.i, Relation.h2, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, Relation.lmd2, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.i, Relation.rmd2, 0, Field.pos_tag));
		add(new FeatureItem<>(Source.j, Relation.lmd2, 0, Field.pos_tag));

		add(new FeatureItem<>(Source.i, Relation.h, 0, Field.dependency_label));
		add(new FeatureItem<>(Source.i, Relation.lns2, 0, Field.dependency_label));
		add(new FeatureItem<>(Source.i, Relation.lmd2, 0, Field.dependency_label));
		add(new FeatureItem<>(Source.i, Relation.rmd2, 0, Field.dependency_label));
		add(new FeatureItem<>(Source.j, Relation.lmd2, 0, Field.dependency_label));

		// boolean features
		addSet(new FeatureItem<>(Source.i, 0, Field.binary));
		addSet(new FeatureItem<>(Source.j, 0, Field.binary));

	}
}
