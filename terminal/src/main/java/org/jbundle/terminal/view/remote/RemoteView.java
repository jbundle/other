/*
 *		don@tourgeek.com
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.terminal.view.remote;

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
import org.jbundle.terminal.view.*;

/**
 * RemoteView - This view base class allows any view to become a view server.
 */
public class RemoteView extends ScreenView
{
	protected boolean m_bRmiServerRunning = false;

	/**
	 * Constructor.
	 */
	public RemoteView()
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public RemoteView(BaseControl screenControl,Properties properties)
	{
		this();
		this.init(screenControl, properties);
	}
	/**
	 * Constructor.
	 */
	public void init(BaseControl screenControl, Properties properties)
	{
		super.init(screenControl, properties);
	}
	/**
	 * Free the resources.
	 */
	public void free()
	{
		super.free();
	}
	/**
	 * Reset this control up to implement these new properties.
	 */
	public void setProperties(Properties properties)
	{
		if ("true".equalsIgnoreCase(properties.getProperty(RemoteViewPropertyView.VIEW_SERVER)))
		{	// Bring up the RMI server if it isn't up already
			if (!m_bRmiServerRunning)
			{
				try {	// This code loads the serial port code which can't be loaded after the RMISecurityManager is loaded.
					Class.forName("javax.comm.CommPortIdentifier");
				} catch (ClassNotFoundException ex)	{
					// Ignore this error
				}

				try	{
					RemoteViewServer obj = new RemoteViewServer(this, properties);
					new Thread(obj).start();
					// Note: There are some concurrency issues here that I ignore - there will rarely be a problem
					m_bRmiServerRunning = true;
				} catch (java.rmi.RemoteException ex)	{
					ex.printStackTrace();
				}
			}
		}
		else
		{	// pend(don) How do I shut this thing down?
		}
		super.setProperties(properties);
	}
	/**
	 * Screen that is used to change the properties.
	 */
	public PropertyView getPropertyView(Properties properties)
	{
		return new RemoteViewPropertyView(this, properties);
	}
}
