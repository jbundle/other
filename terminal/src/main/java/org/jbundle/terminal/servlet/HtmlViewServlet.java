package org.jbundle.terminal.servlet;

/**
 * @(#)BaseServlet.java	0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2000 jbundle.org. All Rights Reserved.
 *		don@tourgeek.com
 */

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.jbundle.terminal.server.*;

import java.rmi.*;
import java.awt.event.*;
import java.awt.*;


/**
 * BaseServlet
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
public class HtmlViewServlet extends BaseServlet
	implements org.jbundle.terminal.TerminalConstants
{
	/*
	 * Get the HTML code from the RMI Server.
	 */
	public String getCurrentView() throws RemoteException
	{
		String strScreen = m_rmiOut.getCurrentView();
		return strScreen;
	}
	/*
	 * Get the name of the RMI Server.
	 */
	public String getServerName()
	{
		return VIEW_SERVER;
	}
}
