call setenvironment.bat
cd ..
rmic -d ./WEB-INF/classes com.tourstudio.terminal.control.rmi.RmiControl
rmic -d ./WEB-INF/classes com.tourstudio.terminal.control.rmi.RmiOutImpl
rmic -d ./WEB-INF/classes com.tourstudio.terminal.server.RemoteServer
cd bin