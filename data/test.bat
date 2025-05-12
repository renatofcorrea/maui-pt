#CHCP 65001
java -Dfile.encoding=UTF-8 -jar ../target/maui-pt.jar test -l docs/corpusci/fulltexts/test30 -m models/standard_model  -v vocabulary/TBCI-SKOS_pt.rdf.gz -f skos -i pt
#pause