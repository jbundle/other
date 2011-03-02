/**
 * Copyright (c) 2000 jbundle.org. All Rights Reserved.
 *		Don_Corley@msn.com
 */
package org.jbundle.terminal.model.remote;

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
import org.jbundle.terminal.server.*;
import org.jbundle.terminal.servlet.*;
import org.jbundle.terminal.view.*;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import javax.comm.*;

/**
 * RemoteModelServer.
 */
public class RemoteModelServer extends RemoteServer
{
	protected BaseModel m_screenModel = null;
	
	/**
	 * Constructor.
	 */
	public RemoteModelServer() throws RemoteException
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public RemoteModelServer(BaseModel screenModel, Properties properties) throws RemoteException
	{
		this();
		this.init(screenModel, properties);
	}
	/**
	 * Constructor.
	 */
	public void init(BaseModel screenModel, Properties properties)
	{
		super.init(properties);
		m_screenModel = screenModel;
	}
	/**
	 * Free the resources.
	 */
	public void free()
	{
		super.free();
	}
	/**
	 * Received a character from the client, send it to the serial port.
	 */
	public String sendThisString(String strString, int iFunction, int iModifiers) throws RemoteException
	{
		BaseView screenView = m_screenModel.getScreenView();
		if (strString != null)
		{
			for (int i = 0; i < strString.length(); i++)
				screenView.getScreenControl().sendCharToControl(strString.charAt(i));
		}
		if (iFunction != 0) if (screenView instanceof ScreenView)
		{
			ScreenKeyHandler keyHandler = ((ScreenView)screenView).getKeyListener();
			Component source = (ScreenView)screenView;
			KeyEvent event = this.convertFunctionToKeyEvent(source, iFunction, iModifiers);
			if (keyHandler != null)
				if (event != null)
			{
				if (event.getKeyChar() == KeyEvent.CHAR_UNDEFINED)
					keyHandler.keyReleased(event);
				else
					keyHandler.keyTyped(event);
			}
		}
		return null;	// Don't send any characters back
	}
	/**
	 * Get the current model.
	 * (Optional).
	 */
	public ModelData getCurrentModel() throws RemoteException
	{
		if (m_screenModel instanceof ScreenModel)
			return new ModelData(((ScreenModel)m_screenModel).getCharacters(), ((ScreenModel)m_screenModel).getAttributes());
		else
			return null;	// never
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
		this.startServer(MODEL_SERVER);
	}
}
