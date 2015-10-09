package edu.emory.mathcs.nlp.component.dep.feature;

import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.dep.DEPFeatureTemplate;
import edu.emory.mathcs.nlp.component.util.feature.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Set;

import static edu.emory.mathcs.nlp.common.util.IOUtils.createObjectXZBufferedInputStream;

/**
 * Created by alexlutz on 10/5/15.
 */
public class DEPFeatureTemplate2 extends DEPFeatureTemplate {

	private final String clusterPath = "./embedding/brown.xz";

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
        add(new FeatureItem<>(Source.i, 0, Field.brown));
        add(new FeatureItem<>(Source.j, 0, Field.brown));
        add(new FeatureItem<>(Source.i, 1, Field.brown));
        add(new FeatureItem<>(Source.j, 1, Field.brown));


		// boolean features
		addSet(new FeatureItem<>(Source.i, 0, Field.binary));   //86.60
		addSet(new FeatureItem<>(Source.j, 0, Field.binary));

//		add(new FeatureItem<>(Source.i, 0, Field.lemma), new FeatureItem<>(Source.i, 0, Field.pos_tag),   //87.45    //86.45total
//				new FeatureItem<>(Source.k, 1, Field.lemma), new FeatureItem<>(Source.k, 1, Field.pos_tag));  //87.40 take this out
		add(new FeatureItem<>(Source.i, 0, Field.lemma), new FeatureItem<>(Source.i, 0, Field.pos_tag),
				new FeatureItem<>(Source.k, 1, Field.lemma));                                                   //87.53!
//        add(new FeatureItem<>(Source.i, 0, Field.lemma), new FeatureItem<>(Source.i, 0, Field.pos_tag),
//                new FeatureItem<>(Source.k, 1, Field.pos_tag));                                                 //87.37
//        add(new FeatureItem<>(Source.i, 0, Field.lemma),
//                new FeatureItem<>(Source.k, 1, Field.lemma), new FeatureItem<>(Source.k, 1, Field.pos_tag));  //87.31
//        add(new FeatureItem<>(Source.i, 0, Field.pos_tag),
//                new FeatureItem<>(Source.k, 1, Field.lemma), new FeatureItem<>(Source.k, 1, Field.pos_tag));    //87.26 slow
//        add(new FeatureItem<>(Source.i, 0, Field.lemma), new FeatureItem<>(Source.k, 1, Field.lemma));          //87.27
//        add(new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.k, 1, Field.pos_tag));      //87.41
//        add(new FeatureItem<>(Source.k, 1, Field.pos_tag), new FeatureItem<>(Source.j,  0, Field.lemma));       //87.31

//        add(new FeatureItem<>(Source.i, 0, Field.lemma), new FeatureItem<>(Source.i, 0, Field.valency, Direction.right));   //87.28   //86.14
//        add(new FeatureItem<>(Source.i,  0, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.valency, Direction.right));    //87.25
//        add(new FeatureItem<>(Source.i,  0, Field.lemma), new FeatureItem<>(Source.i, 0, Field.valency, Direction.left));       //87.24
//		add(new FeatureItem<>(Source.i,  0, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.valency, Direction.left)); //87.40
//        add(new FeatureItem<>(Source.j,  0, Field.lemma), new FeatureItem<>(Source.j, 0, Field.valency, Direction.left));   //lower
		add(new FeatureItem<>(Source.j,  0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.valency, Direction.left)); // up

		//assume optimal above for scores below - 87.55
		add(new FeatureItem<>(Source.i, null, 0, Field.subcategory_label, Direction.all));  //87.70            //86.36
		add(new FeatureItem<>(Source.j, null, 0, Field.subcategory_label, Direction.all));  //87.72     //87.42 no valency direction left
        add(new FeatureItem<>(Source.i, null, 0, Field.subcategory_lemma, Direction.all));  //87.50
//        add(new FeatureItem<>(Source.i, null, 0, Field.subcategory_pos, Direction.all));       //87.39
        add(new FeatureItem<>(Source.j, null, 0, Field.subcategory_pos, Direction.all));     //87.48
        add(new FeatureItem<>(Source.j, null, 0, Field.subcategory_lemma, Direction.all));      //87.63

        add(new FeatureItem<>(Source.k, 0, Field.lemma), new FeatureItem<>(Source.k, 1, Field.lemma), new FeatureItem<>(Source.k, 2, Field.lemma)); //87.55  //87.21
//        add(new FeatureItem<>(Source.k, 0, Field.simplified_word_form), new FeatureItem<>(Source.k, 1, Field.pos_tag), new FeatureItem<>(Source.k, 2, Field.pos_tag));  //87.41
//		suffix and prefix
        add(new FeatureItem<>(Source.i,  0, Field.suffix)); //87.48
        add(new FeatureItem<>(Source.j,  0, Field.suffix));     //87.51 -Base AdaGrad
        add(new FeatureItem<>(Source.k,  0, Field.suffix));     //87.48
//
//        add(new FeatureItem<>(Source.j, 0, Field.prefix));  //87.46
        add(new FeatureItem<>(Source.i, 0, Field.prefix));	//86.31      //87.62
        add(new FeatureItem<>(Source.k, 0, Field.prefix));  //    //87.62

//        add(new FeatureItem<>(Source.k, 0, Field.simplified_word_form), new FeatureItem<>(Source.k, 1, Field.pos_tag)); //87.44
        add(new FeatureItem<>(Source.k, 0, Field.prefix), new FeatureItem<>(Source.k, 1, Field.prefix));  //87.52  //87.26 take this out(works ADD?)
        add(new FeatureItem<>(Source.k, 0, Field.suffix), new FeatureItem<>(Source.k, 1, Field.suffix));    //87.52
//        add(new FeatureItem<>(Source.k, 0, Field.lemma), new FeatureItem<>(Source.k, 1, Field.lemma));      //87.41
        add(new FeatureItem<>(Source.i, 0, Field.lemma), new FeatureItem<>(Source.i, 1, Field.lemma));      //87.57
        add(new FeatureItem<>(Source.j, 0, Field.lemma), new FeatureItem<>(Source.j, 1, Field.lemma));          //87.53
//		add(new FeatureItem<>(Source.i, 0, Field.lemma), new FeatureItem<>(Source.i, 1, Field.pos_tag));        //
//        add(new FeatureItem<>(Source.j, 0, Field.lemma), new FeatureItem<>(Source.j, 1, Field.pos_tag));
        add(new FeatureItem<>(Source.k, 0, Field.lemma), new FeatureItem<>(Source.k, 1, Field.pos_tag));
        add(new FeatureItem<>(Source.j,  0, Field.lemma), new FeatureItem<>(Source.i,  0, Field.path));
        add(new FeatureItem<>(Source.j,  0, Field.pos_tag), new FeatureItem<>(Source.i, 0,  Field.path));

		add(new FeatureItem<>(Source.i,  0, Field.lemma), new FeatureItem<>(Source.i, 0, Field.path));           //87.72
        add(new FeatureItem<>(Source.i,  0, Field.pos_tag), new FeatureItem<>(Source.i, 0,Field.path));         //87.72

//		add(    new FeatureItem<>(Source.j,  0, Field.pos_tag),
//				new FeatureItem<>(Source.j,  Relation.lmd, 0, Field.pos_tag),
//				new FeatureItem<>(Source.j,  Relation.lmd2, 0, Field.pos_tag));	//87.45

		//word distance
//		add(new FeatureItem<>(Source.i, 0, Field.lemma), new FeatureItem<>(Source.i, 0, Field.distance));   //they only add if Right or Left Arc
//		add(new FeatureItem<>(Source.i, 0, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.distance));
//		add(new FeatureItem<>(Source.j, 0, Field.lemma), new FeatureItem<>(Source.j, 0, Field.distance));
//		add(new FeatureItem<>(Source.j, 0, Field.pos_tag), new FeatureItem<>(Source.j, 0, Field.distance));



	}

	protected void initBrownCluster() throws IOException {
		map = (Map<String, Set<String>>) new ObjectInputStream((createObjectXZBufferedInputStream(clusterPath)));
	}

}
