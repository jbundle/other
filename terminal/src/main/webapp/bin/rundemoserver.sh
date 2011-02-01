export CLASSPATH=./classes
java -Djava.rmi.server.codebase=http://www.tourstudio.com/terminal/classes/ -Djava.security.policy=./etc/policy com.tourstudio.terminal.Main properties=./etc/DemoTerminal.properties noscreen=true
