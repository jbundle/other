package org.jbundle.terminal.server;

import java.rmi.Remote; 
import java.rmi.RemoteException;
import java.util.Properties;

/*
 * This is the General-purpose interface for all Remote interfaces.
 * Some classes do not have to supply implementations for all these methods.
 */
public interface RmiOut extends Remote
{
	/*
	 * Initialize the link. Pass the sender's address and the sender's properties.
	 */
	public void setProperties(RmiOut hello, Properties properties) throws RemoteException;
	/*
	 * Send this character.
	 */
	public void sendThisChar(char chChar) throws RemoteException;
	/*
	 * Send this string and get any characters waiting in the buffer.
	 * (Optional).
	 */
	public String sendThisString(String strString, int iFunction, int iModifiers) throws RemoteException;
	/*
	 * Get the current View (in HTML).
	 * (Optional).
	 */
	public String getCurrentView() throws RemoteException;
	/*
	 * Get the current model.
	 * (Optional).
	 */
	public ModelData getCurrentModel() throws RemoteException;
}