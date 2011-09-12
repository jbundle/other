/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.terminal.servlet;

/**
 * @(#)DBServlet.java	0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2000 jbundle.org. All Rights Reserved.
 *		don@tourgeek.com
 */

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.jbundle.terminal.model.*;
import org.jbundle.terminal.server.*;

import java.rmi.*;
import java.awt.event.*;
import java.awt.*;


/**
 * HtmlModelServlet
 * 
 * This servlet is the main servlet.
 * <p>
 * The possible params are:
 * <pre>
 *	record - Create a default HTML screen for this record (Display unless "move" param)
 *	screen - Create this HTML screen
 *	limit - For Displays, limit the records displayed
 *	form - If "yes" display the imput form above the record display
 *	move - HTML Input screen - First/Prev/Next/Last/New/Refresh/Delete
 *	applet - applet, screen=applet screen
 *				applet params: archive/id/width/height/cabbase
 *	menu - Display this menu page
 * </pre>
 */
public class HtmlModelServlet extends BaseServlet
	implements org.jbundle.terminal.TerminalConstants
{
	protected ScreenModel m_screenModel = null;
	protected HtmlViewConverter m_viewConverter = null;		// Utility to convert the model to HTML

	/**
	 * Get the HTML code from the RMI Server.
	 */
	public String getCurrentView() throws RemoteException
	{
		ModelData modelData = m_rmiOut.getCurrentModel();
		if (m_screenModel == null)
			m_screenModel = new ScreenModel(null, null);	// Use as a holder only
		m_screenModel.setCharacters(modelData.getCharacters());
		m_screenModel.setAttributes(modelData.getAttributes());
		if (m_viewConverter == null)
			m_viewConverter = new HtmlViewConverter(m_screenModel);
		else
			m_viewConverter.setModel(m_screenModel);
		String strView = m_viewConverter.getHtmlScreenOut();
		return strView;
	}
	/**
	 * Get the name of the RMI Server.
	 */
	public String getServerName()
	{
		return MODEL_SERVER;
	}
}
