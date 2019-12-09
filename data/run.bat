CHCP 65001

java -Dfile.encoding=UTF-8 -jar ..\target\maui-pt.jar run -l docs\corpusci\fulltexts\test30\Artigo31.txt -m models\model_fulltexts_PortugueseStemmer_train30 -v vocabulary\TBCI-SKOS_pt.rdf.gz -f skos -i pt
pause