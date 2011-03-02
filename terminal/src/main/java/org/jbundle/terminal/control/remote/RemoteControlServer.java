package org.jbundle.terminal.control.remote;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.net.*;
import java.awt.event.*;

import org.jbundle.util.apprunner.*;
import org.jbundle.terminal.*;
import org.jbundle.terminal.control.rmi.*;
import org.jbundle.terminal.control.serial.*;
import org.jbundle.terminal.server.*;

import javax.comm.*;

/**
 * This is the remote process that gets its commands and talks to a control.
 * This is why I implement BaseModel, so the control will think I'm a model and send me commands.
 */
public class RemoteControlServer extends RemoteServer
{
	protected RmiOut m_rmiClient = null;		// So I can talk back to my client
	public BaseControl m_screenControl = null;

	public RemoteControlServer()
		throws RemoteException
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public RemoteControlServer(BaseControl screenControl, Properties properties)
		throws RemoteException
	{
		this();
		this.init(screenControl, properties);
	}
	/**
	 * Constructor.
	 */
	public void init(BaseControl screenControl, Properties properties)
	{
		this.init(properties);
		m_screenControl = screenControl;
	}
	/**
	 * Free the resources.
	 */
	public void free()
	{
		super.free();
	}
	/**
	 * Save the client reference (client must call this first).
	 * This is used to send characters back.
	 */
	public void setProperties(RmiOut rmiClient, Properties properties) throws RemoteException
	{
		m_rmiClient = rmiClient;
	}
	/**
	 * Received a character from the client, send it to the serial port.
	 */
	public void sendThisChar(char chChar) throws RemoteException
	{
		m_screenControl.sendCharToControl(chChar);	// As if it was typed directly
	}
	/**
	 * Got a character from the serial port, send it to the client.
	 */
	public void sendCharToControl(char chChar)
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
	 * When an object implementing interface <code>Runnable</code> is used
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
		this.startServer(CONTROL_SERVER);
	}
}