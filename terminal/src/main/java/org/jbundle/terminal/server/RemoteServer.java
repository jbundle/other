/**
 * Copyright (c) 2000 jbundle.org. All Rights Reserved.
 *		Don_Corley@msn.com
 */
package org.jbundle.terminal.server;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import org.jbundle.util.apprunner.*;
import org.jbundle.terminal.*;
import org.jbundle.terminal.control.*;
import org.jbundle.terminal.control.serial.*;
import org.jbundle.terminal.model.*;
import org.jbundle.terminal.servlet.*;
import org.jbundle.terminal.view.*;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import javax.comm.*;

/**
 * The base RMI Server.
 */
public class RemoteServer extends UnicastRemoteObject 
	implements RmiOut, Runnable, TerminalConstants
{
	
	/**
	 * Constructor.
	 */
	public RemoteServer() throws RemoteException
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public RemoteServer(Properties properties) throws RemoteException
	{
		this();
		this.init(properties);
	}
	/**
	 * Constructor.
	 */
	public void init(Properties properties)
	{
	}
	/*
	 * Free the resources.
	 */
	public void free()
	{
	}
	/*
	 * Received a character from the client, send it to the serial port.
	 */
	public String sendThisString(String strString, int iFunction, int iModifiers) throws RemoteException
	{
		return null;	// Don't send any characters back
	}
	public void setProperties(RmiOut hello, Properties properties) throws RemoteException {
	}
	public void sendThisChar(char chChar) throws RemoteException {
	}
	public String getCurrentView() throws RemoteException {
		return null;
	}
	/** When an object implementing interface <code>Runnable</code> is used
	 * to create a thread, starting the thread causes the object's
	 * <code>run</code> method to be called in that separately executing
	 * thread.
	 * <p>
	 * The general contract of the method <code>run</code> is that it may
	 * take any action whatsoever.
	 *
	 * @see     java.lang.Thread#run()
	 */
	public void run()
	{
	}
	public void startServer(String strServerName)
	{
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
			// Bind this object instance to the name "HelloServer"
			InetAddress iNetAddr = InetAddress.getLocalHost();
			String strHostName = iNetAddr.getHostName();
			String strName = "//" + strHostName + "/" + strServerName;
			Naming.rebind(strName, this);
			if (DEBUG)
				System.out.println(strName + " bound in registry");
		} catch (Exception e) {
			System.out.println("HelloImpl err: " + e.getMessage());
			e.printStackTrace();
		}
	}
	/*
	 * Convert this function to a key event.
	 */
	public ModelData getCurrentModel() throws RemoteException {
		return null;
	}
	/*
	 * Convert this function to a key event.
	 */
	public KeyEvent convertFunctionToKeyEvent(Component source, int iFunction, int iModifiers)
	{
		int id = 0;
		long when = 0;
		int modifiers = iModifiers;
		int keyCode = iFunction;
		char keyChar = 0;
		switch (keyCode)
		{
		case KeyEvent.VK_ESCAPE:	// Processed in keyChar()
			keyChar = Event.ESCAPE;break;
		case KeyEvent.VK_TAB:	// Processed in keyChar()
			keyChar = Event.TAB;break;
		case KeyEvent.VK_SPACE:	// Processed in keyChar()
			keyChar = ' ';break;
		case KeyEvent.VK_BACK_SPACE:
			keyChar = Event.BACK_SPACE;break;
		case KeyEvent.VK_ENTER:
			keyChar = Event.ENTER;break;
		default:
			keyChar = KeyEvent.CHAR_UNDEFINED;break;
		}
		if (((iModifiers & Event.CTRL_MASK) == 0) &
			((iModifiers & Event.ALT_MASK) == 0))
		{
			if ((keyCode >= KeyEvent.VK_A)
				&& (keyCode <= KeyEvent.VK_Z))
			{
				if ((iModifiers & Event.SHIFT_MASK) == 0)
					keyChar = (char)('a' + keyCode - KeyEvent.VK_A);
				else
					keyChar = (char)('A' + keyCode - KeyEvent.VK_A);
			}
			else if ((keyCode >= KeyEvent.VK_0)
				&& (keyCode <= KeyEvent.VK_9))
			{
				keyChar = (char)('0' + keyCode - KeyEvent.VK_0);
			}
		}
		if ((iModifiers & Event.CTRL_MASK) != 0)
		{
			if ((keyCode >= KeyEvent.VK_A)
				&& (keyCode <= KeyEvent.VK_Z))
					keyChar = (char)(1 + keyCode - KeyEvent.VK_A);	// ^a = 1.
		}
		KeyEvent event = new KeyEvent(source, id, when, modifiers, keyCode, keyChar);
		return event;
	}
}
