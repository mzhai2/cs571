Malt Model: [Download](https://drive.google.com/file/d/0B3akpSg7pbnLUlBWVjUyLUdaOEk/view?usp=sharing)
Stanford Model: [Download](https://drive.google.com/file/d/0B3akpSg7pbnLQkNfWmNEZXdHaUE/view?usp=sharing)

####To train project, you must call the DEPTrain class.
```
java edu.emory.mathcs.nlp.bin.DEPTrain -c <filename> -t <filepath> -d <filepath> [-f <integer> -m <filename> -te <string> -de <string>]

-c <filename> : configuration file (required)
-f <integer>  : feature template ID (default: 0)
-m <filename> : model file (optional)
-t <filepath> : training path (required)
-d <filepath> : development path (required)
-te <string>  : training file extension (default: *)
-de <string>  : development file extension (default: *)
```

* `-c` The default configuration: [`config_train_pos.xml`](../../src/main/resources/configuration/config_train_pos.xml) (see [below](#configuration) for more details).
* `-f` The default feature template, `0`, is defined in [`POSFeatureTemplate`](../../src/main/java/edu/emory/mathcs/nlp/component/pos/POSFeatureTemplate.java). You can define your own feature templates and declare them in [`POSTrain`](../../src/main/java/edu/emory/mathcs/nlp/bin/POSTrain.java), which is useful for feature engineering (developers only).
* `-m` If specified, the best statistical model is saved to the file as a compressed Java object.
* `-t|d` The training or development path can point to either a file or a directory. When the path points to a file, only the specific file is trained. When the path points to a directory, all files with the file extension `-te|de` under the specific directory are trained.
* `-te|de` The training or development file extensions specifies the extensions of the training and development files. The default value `*` implies files with any extension. This option is used only when the training or development path `-t|d` points to a directory.

###The following is the description for the configuration xml
| Field | Description |
| :-----: | :---------- |
| `<language>` | Specifies the [language](https://github.com/emorynlp/common/blob/master/src/main/java/edu/emory/mathcs/nlp/common/util/Language.java) of the input data. |
| `<tsv>` | Specifies the [Tab-Separated-Values](https://en.wikipedia.org/wiki/Tab-separated_values) format used in the input data. |
| `<column>` | Specifies the columns in TSV.<ul><li>`index` specifies the index of the field, starting at `0`.</li><li>`field` specifies the name of the field.</li>&#9702; `id`: node ID.<br>&#9702; `form`: word form.<br>&#9702; `lemma`: lemma.<br>&#9702; `pos`: part-of-speech tag.<br>&#9702; `feats`: pre-defined features.<br>&#9702; `headID`: head node ID.<br>&#9702; `deprel`: dependency label.<br>&#9702; `nament`: named entity tag.<br>&#9702; `sheads`: semantic heads.</ul> |
| `<optimizer>` | Specifies the optimizer and its parameters for training (see [below](#optimizers)).<ul><li>`algorithm`: the name of the optimization algorithm.</li><li>`label_cutoff`: discard labels appearing less than this cutoff.</li><li>`feature_cutoff`: discard features appearing less than this cutoff.</li><li>`reset_weights`: if `true`, reset the weight vector to `0` before self-training.</li><li>`thread_size`: the number of threads (for one-vs-all learning).</li><li>`average`: if `true`, return the averaged weight vector (for online learning).</li><li>`learning_rate`: the learning rate.</li><li>`bias`: the bias weight.</li><li>`batch_ratio`: the portion of each mini-batch (e.g., use every 10% as a mini-batch).</li></ul>| 
| `<aggregate>` | If set, use disjoint aggregation (DAGGER).<ul><li>`tolerance`: tolerance of termination criterion.</li></ul> | 

###The following is an example for a configuration model for the Malt parser.


```
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
<algorithm>adagrad-mini-batch</algorithm>
<label_cutoff>4</label_cutoff>
<feature_cutoff>7</feature_cutoff>
<reset_weights>false</reset_weights>
<average>false</average>
<learning_rate>0.09</learning_rate>
<batch_ratio>0.1</batch_ratio>
<bias>1</bias>
</optimizer>

<aggregate tolerance_delta="0.01" max_tolerance="5"/>
<ambiguity_class_threshold>0.4</ambiguity_class_threshold>
</configuration>
```

###In order to decode with an existing model you will need to load into an existing model, the test file, and an output file

```
edu.emory.mathcs.nlp.bin.DEPDecode 
args[0]: path2ModelFile
args[1]: path2TestFile
args[2]: path2OutputFile
```