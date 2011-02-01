export CLASSPATH=$CLASSPATH:classes
java -Djava.rmi.server.hostname=www.tourstudio.com -Djava.rmi.codebase=http://www.tourstudio.com/loaddist/classes/ -Djava.security.policy=/data/java/tour/bin/policy/policy.all com.tourstudio.samplermi.SampleRMIServer www.tourstudio.com:1099
