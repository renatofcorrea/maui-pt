CHCP 65001

java -Dfile.encoding=UTF-8 -jar ..\target\maui-pt.jar train -l docs\corpusci\fulltexts\train30 -m models\model_fulltexts_PortugueseStemmer_train30 -v vocabulary\TBCI-SKOS_pt.rdf -f skos -o 2 -i pt
pause