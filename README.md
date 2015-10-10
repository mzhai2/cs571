Malt Model: [Download](https://drive.google.com/file/d/0B3akpSg7pbnLUlBWVjUyLUdaOEk/view?usp=sharing)
Stanford Model: [Download](https://drive.google.com/file/d/0B3akpSg7pbnLQkNfWmNEZXdHaUE/view?usp=sharing)

In order to train the dependency parser, you will need to call the `DEPTrain` class under the this projec and provide sufficient information for training the parser.

edu.emory.mathcs.nlp.bin.DEPTrain
-b <integer>      : beam size(default: 1) (default: 1)
-bThres <double>  : Instance cutoff threshold (default: 0.2) (default: 0.20)
-c <filename>     : confinguration file (required)
-d <filepath>     : development path (required)
-de <string>      : development file extension (default: *) (default: *)
-f integer        : feature template ID (default: 0) (default: 0)
-m <filename>     : model file (optional)
-t <filepath>     : training path (required)
-tExport <string> : path to save the result t (default: null)
-te <string>      : training file extension (default: *) (default: *)
-tst <filepath>   : test path (required)
-tste <string>    : test file extension (default: *) (default: *)

The following is an example for a configuration model for the Malt parser.

<configuration>
<language>english</language>

<tsv>
<column index="1" field="form"/>
<column index="2" field="lemma"/>
<column index="3" field="pos"/>
<column index="4" field="feats"/>
<column index="5" field="headID"/>
<column index="6" field="deprel"/>
</tsv>

<optimizer>
<algorithm>adagrad</algorithm>
<label_cutoff>4</label_cutoff>
<feature_cutoff>2</feature_cutoff>
<reset_weights>false</reset_weights>
<average>false</average>
<learning_rate>0.02</learning_rate>
<bias>0.1</bias>
</optimizer>

<aggregate tolerance_delta="0.01" max_tolerance="5"/>
<ambiguity_class_threshold>0.4</ambiguity_class_threshold>
</configuration>

In order to decode with an existing model you will need to load into an existing model, the test file, and an output file

edu.emory.mathcs.nlp.bin.DEPDecode 
args[0]: path2ModelFile
args[1]: path2TestFile
args[2]: path2OutputFile
