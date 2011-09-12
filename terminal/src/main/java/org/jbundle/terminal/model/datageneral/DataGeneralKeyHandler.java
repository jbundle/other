/*
 *		Don_Corley@msn.com
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.terminal.model.datageneral;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

import org.jbundle.terminal.*;
import org.jbundle.terminal.model.*;


/**
 * DataGeneralKeyHandler - Represents the computer serialOut.
 * The computer serialOut received key clicks from the keyboard 
 * and data from the host, then figures out what to do with it.
 * If this is a full-duplex dumb serialOut, keyboard data is sent to
 * the external out port and in data is sent to the screen representation.
 * For convience, this object implements KeyListener, but ultimately
 * characters must be passed in using the method 'charIn'.
 * Then characters are sent out to the BaseModel 'charOut' method.
 */
public class DataGeneralKeyHandler extends ScreenKeyHandler
	implements KeyListener,
				DataGeneralConst
{
	/**
	 * Constructor.
	 */
	public DataGeneralKeyHandler()
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public DataGeneralKeyHandler(BaseControl serialOut)
	{
		this();
		this.init(serialOut);
	}
	/**
	 * Constructor.
	 */
	public void init(BaseControl serialOut)
	{
		super.init(serialOut);
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
			case '\b':
				this.sendCharToControl(kDelete);
				break;
			case '\r':
			case '\n':
				if (!event.isShiftDown())
					this.sendCharToControl(kNewLine);
				else
					this.sendCharToControl(kCReturn);
				break;
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
		if (event.getKeyChar() == KeyEvent.CHAR_UNDEFINED)
		{	// Not passed by keyTyped(xx)
			switch (event.getKeyCode())
			{
				case KeyEvent.VK_HOME:
					this.sendCharToControl(kHome);
					break;
				case KeyEvent.VK_LEFT:
					this.sendCharToControl(kLeft);
					break;
				case KeyEvent.VK_RIGHT:
					this.sendCharToControl(kRight);
					break;
				case KeyEvent.VK_DOWN:
					this.sendCharToControl(kDown);
					break;
				case KeyEvent.VK_UP:
					this.sendCharToControl(kUp);
					break;
				case KeyEvent.VK_DELETE:
					this.sendCharToControl(kDelete);
					break;
				case KeyEvent.VK_CLEAR:
					this.sendCharToControl(kClearScreen);
					break;
				case KeyEvent.VK_END:
					this.sendCharToControl(kEndCur);
					break;
				case KeyEvent.VK_INSERT:
					this.sendCharToControl(kInsCur);
					break;
				case KeyEvent.VK_PAGE_UP:
					if (kCHdr != 0)
						this.sendCharToControl(kCHdr);
					this.sendCharToControl(kC2Key);
					break;
				case KeyEvent.VK_PAGE_DOWN:
					if (kCHdr != 0)
						this.sendCharToControl(kCHdr);
					this.sendCharToControl(kC4Key);
					break;
				case KeyEvent.VK_F1:
				case KeyEvent.VK_F2:
				case KeyEvent.VK_F3:
				case KeyEvent.VK_F4:
				case KeyEvent.VK_F5:
				case KeyEvent.VK_F6:
				case KeyEvent.VK_F7:
				case KeyEvent.VK_F8:
				case KeyEvent.VK_F9:
				case KeyEvent.VK_F10:
				case KeyEvent.VK_F11:
				case KeyEvent.VK_F12:
					int iOffset = kFun0;
					if (event.isShiftDown())
						iOffset = kFun1;
					if (event.isControlDown())
						iOffset = kFun2;
					if ((event.isShiftDown()) && (event.isControlDown()))
						iOffset = kFun3;
					this.sendCharToControl(kFuncHdr);
					this.sendCharToControl((char)(iOffset + event.getKeyCode() - KeyEvent.VK_F1));
					break;
				case KeyEvent.VK_ESCAPE:	// Processed in keyChar()
				case KeyEvent.VK_TAB:	// Processed in keyChar()
				case KeyEvent.VK_SPACE:	// Processed in keyChar()
				case KeyEvent.VK_BACK_SPACE:
				case KeyEvent.VK_ENTER:
				default:
			}
		}
	}
}
