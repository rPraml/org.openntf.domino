/C/Program\ Files/IBM/Domino/nserver.exe -c "tell http q"
mvn install  -DskipTests
rm ../../eclipse/plugins/*.*
cp domino/updatesite/target/repository/plugins/* ../../eclipse/plugins/
/C/Program\ Files/IBM/Domino/nserver.exe -c "load http"