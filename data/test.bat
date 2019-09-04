CHCP 65001
java -Dfile.encoding=UTF-8 -jar ..\target\maui-pt.jar test -l docs\corpusci\full_texts\test30 -m models\testmodel -v  vocabulary\TBCI-SKOS_pt.rdf.gz -f skos -i pt
pause