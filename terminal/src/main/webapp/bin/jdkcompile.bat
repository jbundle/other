call setenvironment.bat
cd ..
cd src
Javac -deprecation -d ../WEB-INF/classes *.java
cd ..
cd scripts