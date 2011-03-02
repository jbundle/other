/*
 * Copyright (c) 2000 jbundle.org. All Rights Reserved.
 *		don@tourgeek.com
 */	
package org.jbundle.terminal.control.rmi;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.net.*;
import java.awt.event.*;

import org.jbundle.util.apprunner.*;
import org.jbundle.terminal.*;
import org.jbundle.terminal.control.serial.*;
import org.jbundle.terminal.server.*;

import javax.comm.*;

/*
 * This is the remote process that gets its commands and talks to a control.
 * This is why I implement BaseModel, so the control will think I'm a model and send me commands.
 */
public class RmiOutImpl extends UnicastRemoteObject
	implements RmiOut, BaseModel, TerminalConstants
{
	protected RmiOut m_rmiClient = null;		// So I can talk back to my client
	public BaseControl m_screenControl = null;

	public RmiOutImpl() throws RemoteException
	{
		super();
	}
	public static void main(String args[])
	{
		// This code loads the serial port code which can't be loaded after the RMISecurityManager is loaded.
		try {
			 CommPortIdentifier.getPortIdentifier("COM1");
		} catch (NoSuchPortException e) {
			System.out.println(e.getMessage());
		}
	    // create a registry if one is not running already.
	    try {
			java.rmi.registry.LocateRegistry.createRegistry(1099);
	    } catch (java.rmi.server.ExportException ee) {
			// registry already exists, we'll just use it.
	    } catch (RemoteException re) {
			System.err.println(re.getMessage());
			re.printStackTrace();
	    }
	    
		// Create and install a security manager 
		if (System.getSecurityManager() == null)
		{
			System.setSecurityManager(new RMISecurityManager()); 
		} 

		try {
			RmiOutImpl obj = new RmiOutImpl();
			// Bind this object instance to the name "HelloServer"
			InetAddress iNetAddr = InetAddress.getLocalHost();
			String strHostName = iNetAddr.getHostName();
			Naming.rebind("//" + strHostName + '/' + CONTROL_SERVER, obj);
			if (DEBUG)
				System.out.println(CONTROL_SERVER + " bound in registry");
		} catch (Exception e) {
			System.out.println("HelloImpl err: " + e.getMessage());
			e.printStackTrace();
		}
	}
	/**
	 * Save the client reference (client must call this first).
	 * This is used to send characters back.
	 */
	public void setProperties(RmiOut rmiClient, Properties properties) throws RemoteException
	{
		m_rmiClient = rmiClient;

		// create the default model, view, and control.
		if (m_screenControl != null)
			m_screenControl.free();

		String strRemoteClassName = null;
		if (properties != null)
		{
			strRemoteClassName = properties.getProperty(RmiPropertyView.SERVER_CONTROL_PARAM);
			if ((strRemoteClassName == null)
				|| (strRemoteClassName.length() == 0)
				|| (strRemoteClassName.equalsIgnoreCase("(Use remote properties)")))
					properties = null;
		}
		if (properties == null)
			properties = Utility.getProperties(null, new Properties());
		String strClassName = properties.getProperty(MainPropertyView.CONTROL_PARAM);
		strRemoteClassName = properties.getProperty(RmiPropertyView.SERVER_CONTROL_PARAM);
		if ((strRemoteClassName == null)
			|| (strRemoteClassName.length() == 0)
			|| (strRemoteClassName.equalsIgnoreCase("(Use remote properties)")))
				strRemoteClassName = "Serial";
		properties.setProperty(MainPropertyView.CONTROL_PARAM, strRemoteClassName);
		m_screenControl = Utility.createControlFromProperties(this, properties);
		properties.setProperty(MainPropertyView.CONTROL_PARAM, strClassName);
	}
	/**
	 * Received a character from the client, send it to the serial port.
	 */
	public void sendThisChar(char chChar) throws RemoteException
	{
		m_screenControl.sendCharToControl(chChar);
	}
	/**
	 * Got a character from the serial port, send it to the client.
	 */
	public void sendChar(char chChar)
	{
		try	{
			if (m_rmiClient != null)
				m_rmiClient.sendThisChar(chChar);
		} catch (RemoteException ex)	{
			System.out.println("Error on addChar: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
	}
	/**
	 * Set up the model.
	 */
	public void init(BaseView screenView, Properties properties)
	{
		// Note required in this implementation
	}
	/**
	 * Free this object.
	 */
	public void free()
	{
		// Note required in this implementation
	}
	/**
	 * Get screen model.
	 */
	public BaseView getScreenView()
	{
		return null;
	}
	/**
	 * Get screen model.
	 */
	public void setScreenView(BaseView screenView)
	{
	}
	/**
	 * Reset this control up to implement these new properties.
	 */
	public void setProperties(Properties properties)
	{
		// Add code here.
	}
	/**
	 * Create a listener for this control.
	 */
	public org.jbundle.terminal.model.ScreenKeyHandler createKeyListener(BaseControl screenControl)
	{
	    return null;
	}
	/**
	 * Screen that is used to change the properties.
	 */
	public PropertyView getPropertyView(Properties properties)
	{
		return null;	// Not used in this class
	}
	public String sendThisString(String strString,int iFunction,int iModifiers) throws RemoteException {
		return null;	// Not implemented
	}
	public String getCurrentView() throws RemoteException {
		return null;	// Not implemented
	}
	public ModelData getCurrentModel() throws RemoteException {
		return null;	// Not implemented
	}
}