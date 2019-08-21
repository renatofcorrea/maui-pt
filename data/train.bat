CHCP 65001
java -Dfile.encoding=UTF-8 -jar..\target\maui-pt-1.1.jar train -l docs\train30 -m models\testmodel -v vocabulary\TBCI-SKOS_pt.rdf.gz -f skos -o 2 -i pt
pause