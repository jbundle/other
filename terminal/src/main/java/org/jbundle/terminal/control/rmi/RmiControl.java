/* @(#)RmiControl.java	1.6 98/07/17 SMI
 *
 * Copyright (c) 1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 */
package org.jbundle.terminal.control.rmi;

import javax.comm.*;
import java.io.*;
import java.util.*;
import java.awt.TextArea;
import java.awt.event.*;
import java.util.TooManyListenersException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.*;

import java.rmi.RMISecurityManager;
import java.rmi.server.UnicastRemoteObject;

import org.jbundle.util.apprunner.*;
import org.jbundle.terminal.*;
import org.jbundle.terminal.model.*;
import org.jbundle.terminal.server.*;

/**
A class that handles the details of a serial connection. Reads from one 
TextArea and writes to a second TextArea. 
Holds the state of the connection.
*/
public class RmiControl extends UnicastRemoteObject
	implements BaseControl, RmiOut, TerminalConstants
//x extends ScreenControl
{
	protected RmiOut m_rmiOut = null;
	/**
	 * Screen object to send characters to.
	 */
	protected BaseModel m_screenModel = null;

	/**
	 * Constructor.
	 */
	public RmiControl() throws RemoteException
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public RmiControl(BaseModel screenModel, Properties properties) throws RemoteException
	{
		this();
		this.init(screenModel, properties);
	}
	/**
	 * Constructor.
	 */
	public void init(BaseModel screenModel, Properties properties)
	{
		this.setScreenModel(screenModel);
		this.setProperties(properties);		// Get the properties and set up this control
	}
	/**
	 * Free the resources.
	 */
	public void free()
	{
		if (m_screenModel != null)
			if (m_screenModel.getScreenView() != null)
				if (m_screenModel.getScreenView().getScreenControl() == this)
					m_screenModel.getScreenView().setScreenControl(null);
		m_screenModel = null;
	}
	/**
	 * Set this control up to implement these properties.
	 */
	public void setProperties(Properties properties)
	{
		try {
			String strServerName = properties.getProperty(RmiPropertyView.SERVER_PARAM);			// Get the server name
			if ((strServerName == null) || (strServerName.length() == 0))
				strServerName = "[codebase]";
//+			if (strServerName.equalsIgnoreCase("[codebase]")
//+				strServerName = getCodeBase().getHost();
			if ((strServerName == null)
				|| (strServerName.length() == 0)
				|| (strServerName.equalsIgnoreCase("[codebase]")))
					strServerName = InetAddress.getLocalHost().getHostName();
			if (strServerName.indexOf('/') == 0)
				strServerName = strServerName.substring(1);
			if (strServerName.indexOf('/') == 0)
				strServerName = strServerName.substring(1);
			if (strServerName.indexOf('/') == strServerName.length())
				strServerName = strServerName.substring(0, strServerName.length() - 1);
			m_rmiOut = (RmiOut)Naming.lookup("//" + strServerName + '/' + CONTROL_SERVER);

//+		Do this in a new thread!
			m_rmiOut.setProperties(this, properties);		// Send my properties to the remote host

//			System.out.println("Message: " + message);
		} catch (Exception e) {
			System.out.println("HelloApplet exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	/**
	 * Get screen.
	 */
	public BaseModel getScreenModel()
	{
		return m_screenModel;
	}
	/**
	 * Get screen.
	 */
	public void setScreenModel(BaseModel screenModel)
	{
		m_screenModel = screenModel;
	}
	/**
	 * Send this character to the remote server.
	 */
	public void sendCharToControl(char chChar)
	{
		try	{
			if (m_rmiOut != null)
				m_rmiOut.sendThisChar(chChar);
		} catch (Exception e) {
			System.out.println("HelloApplet exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	/**
	 * Not used on the client side.
	 */
	public void setProperties(RmiOut hello, Properties properties) throws RemoteException	{
		// Not used on this side
	}
	/**
	 * Receive a character from the remote server, then pass it on to the screen model.
	 * <p>Warning: This is usually done in an independent thread.
	 */
	public void sendThisChar(char chChar) throws RemoteException
	{
		if (m_screenModel != null)
			m_screenModel.sendChar(chChar);
	}
	/**
	 * Screen that is used to change the properties.
	 */
	public PropertyView getPropertyView(Properties properties)
	{
		return new RmiPropertyView(this, properties);
	}
	public String sendThisString(String strString,int iFunction,int iModifiers) throws RemoteException {
		return null;	// Not implemented
	}
	public ModelData getCurrentModel() throws RemoteException {
		return null;	// Not implemented
	}
	public String getCurrentView() throws RemoteException {
		return null;	// Not implemented
	}
}