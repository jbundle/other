call setenv.bat
cd ..
java -Djava.rmi.server.codebase=http://hd2-13.irv.zyan.com/terminal/ -Djava.security.policy=%TERM_ROOT%etc/policy com.tourstudio.terminal.Main
cd bin
