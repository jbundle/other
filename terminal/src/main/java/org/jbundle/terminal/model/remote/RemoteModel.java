/*
 *		don@tourgeek.com
 * Copyright © 2012 jbundle.org. All rights reserved.
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
import org.jbundle.terminal.view.*;

/**
 * RemoteModel.
 */
public class RemoteModel extends ScreenModel
{
	protected boolean m_bRmiServerRunning = false;

	/**
	 * Constructor.
	 */
	public RemoteModel()
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public RemoteModel(BaseView screenPanel, Properties properties)
	{
		this();
		this.init(screenPanel, properties);
	}
	/**
	 * Set up the model.
	 */
	public void init(BaseView screenPanel, Properties properties)
	{
		super.init(screenPanel, properties);
	}
	/**
	 * Free - Clean up.
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
		if ("true".equalsIgnoreCase(properties.getProperty(RemoteModelPropertyView.MODEL_SERVER)))
		{	// Bring up the RMI server if it isn't up already
			if (!m_bRmiServerRunning)
			{
				try {	// This code loads the serial port code which can't be loaded after the RMISecurityManager is loaded.
					Class.forName("javax.comm.CommPortIdentifier");
				} catch (ClassNotFoundException ex)	{
					// Ignore this error
				}

				try	{
					RemoteModelServer obj = new RemoteModelServer(this, properties);
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
		return new RemoteModelPropertyView(this, properties);
	}
}
