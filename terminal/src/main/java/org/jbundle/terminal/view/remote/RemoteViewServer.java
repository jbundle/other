/**
 * Copyright (c) 2000 jbundle.org. All Rights Reserved.
 *		don@tourgeek.com
 */
package org.jbundle.terminal.view.remote;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import org.jbundle.jproperties.*;
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
 * RemoteViewServer - The physical display screen.
 */
public class RemoteViewServer extends RemoteServer
{
	protected BaseView m_screenView = null;
	protected HtmlViewConverter m_viewConverter = null;		// Utility to convert the model to HTML

	/**
	 * Constructor.
	 */
	public RemoteViewServer() throws RemoteException
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public RemoteViewServer(BaseView screenView,Properties properties) throws RemoteException
	{
		this();
		this.init(screenView, properties);
	}
	/**
	 * Constructor.
	 */
	public void init(BaseView screenView, Properties properties)
	{
		super.init(properties);
		m_screenView = screenView;
	}
	/*
	 * Free the resources.
	 */
	public void free()
	{
		super.free();
	}
	/*
	 * Received a character from the client, send it to the serial port.
	 */
	public String sendThisString(String strString, int iFunction, int iModifiers) throws RemoteException
	{
		if (strString != null)
		{
			for (int i = 0; i < strString.length(); i++)
				m_screenView.getScreenControl().sendCharToControl(strString.charAt(i));
		}
		if (iFunction != 0) if (m_screenView instanceof ScreenView)
		{
			ScreenKeyHandler keyHandler = ((ScreenView)m_screenView).getKeyListener();
			Component source = (ScreenView)m_screenView;
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
	/*
	 *
	 */
	public String getCurrentView() throws RemoteException {
		ScreenModel screenModel = (ScreenModel)m_screenView.getScreenControl().getScreenModel();
		if (m_viewConverter == null)
			m_viewConverter = new HtmlViewConverter(screenModel);
		else
			m_viewConverter.setModel(screenModel);
		String strView = m_viewConverter.getHtmlScreenOut();
		return strView;
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
		this.startServer(VIEW_SERVER);
	}
}
