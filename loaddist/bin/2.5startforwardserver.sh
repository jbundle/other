export CLASSPATH=$CLASSPATH:classes
java -Djava.rmi.server.hostname=www.tourstudio.com -Djava.rmi.codebase=http://www.tourstudio.com/loaddist/classes/ -Djava.security.policy=/data/java/tour/bin/policy/policy.all com.tourstudio.forward.ForwardRMIServer www.tourstudio.com:8001 www.tourstudio.com:8002

