/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.terminal.rc;

/**
 * @(#)DBServlet.java	0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 jbundle.org. All Rights Reserved.
 *		Don_Corley@msn.com
 */

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.jbundle.terminal.*;
import org.jbundle.terminal.server.*;

import java.rmi.*;
import java.awt.event.*;
import java.awt.*;


/**
 * DBServlet
 * 
 * This is the base remote control application.
 */
public class RemoteControlApp extends Main
	implements TerminalConstants
{
	/**
	 * Support for running this applet as a standalone application.
	 */
    public static void main(String[] args)
	{
		String vers = System.getProperty("java.version");
		if (vers.compareTo("1.2.0") < 0) {
			System.out.println("!!!WARNING: This program must be run with a " +
							   "1.2.0 or higher version VM!!!");
		}
		if (m_applet == null)
			m_applet = new RemoteControlApp();
		Main.main(args);
	}
    /**
     * Called to start the applet.  You never need to call this directly; it
     * is called when the applet's document is visited.
     */
    public void start()
    {
		super.start();
		RemoteControlThread remoteControlThread = new RemoteControlThread(m_screenView.getScreenControl().getScreenModel());
		remoteControlThread.start();
    }
}
