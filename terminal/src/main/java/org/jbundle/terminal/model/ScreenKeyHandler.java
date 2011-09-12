/*
 *		don@tourgeek.com
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.terminal.model;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

import org.jbundle.terminal.*;


/**
 * ScreenKeyHandler - Represents the computer serialOut.
 * The computer serialOut received key clicks from the keyboard 
 * and data from the host, then figures out what to do with it.
 * If this is a full-duplex dumb serialOut, keyboard data is sent to
 * the external out port and in data is sent to the screen representation.
 * For convience, this object implements KeyListener, but ultimately
 * characters must be passed in using the method 'charIn'.
 * Then characters are sent out to the BaseModel 'charOut' method.
 */
public class ScreenKeyHandler extends Object
	implements KeyListener
{
	/**
	 * Screen object to send characters to.
	 */
	protected BaseControl m_screenControl = null;

	/**
	 * Constructor.
	 */
	public ScreenKeyHandler()
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public ScreenKeyHandler(BaseControl screenControl)
	{
		this();
		this.init(screenControl);
	}
	/**
	 * Constructor.
	 */
	public void init(BaseControl screenControl)
	{
		m_screenControl = screenControl;
	}
	/**
	 * Key typed on the keyboard.
	 */
	public void keyTyped(KeyEvent event)
	{
		char chKey = event.getKeyChar();
		// Send character to the serial port
		switch (chKey)
		{
			// Add processing for special key filtering
			default:
				this.sendCharToControl(chKey);	// Send this character to the control
		}
		event.consume();	// Key handled
	}
	/**
	 * Key typed on the keyboard.
	 */
	public void keyPressed(KeyEvent event)
	{		// HACK this is the only one that works in MS IE4
	}
	/**
	 * Key released on the keyboard.
	 */
	public void keyReleased(KeyEvent event)
	{
		// Overrride for special/control key handling
	}
	/**
	 * Character(s) received from serial port.
	 */
	public void sendCharToControl(char chKey)
	{
		if (m_screenControl != null)
			m_screenControl.sendCharToControl(chKey);	// Send this character to the screen
	}
}
