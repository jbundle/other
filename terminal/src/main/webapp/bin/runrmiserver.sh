. setenv.sh
cd ..
java -Djava.rmi.server.codebase=http://www.tourstudio.com/terminal/classes/ -Djava.security.policy=./etc/policy com.tourstudio.terminal.Main
properties=./etc/ServerTerminal.properties
cd bin
