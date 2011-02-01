call setenvironment.bat
cd ..
java -Djava.rmi.server.codebase=file:%TERM_ROOT%classes/ -Djava.security.policy=%TERM_ROOT%etc/policy com.tourstudio.terminal.Main
cd bin