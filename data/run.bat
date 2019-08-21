CHCP 65001
java -Dfile.encoding=UTF-8 -jar ..\target\maui-pt-1.1.jar run -l docs\test_custom\PLN.txt -m models\testmodel -v vocabulary\TBCI-SKOS_pt.rdf.gz -f skos -i pt
pause