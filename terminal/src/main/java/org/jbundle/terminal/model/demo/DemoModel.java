/**
 * Copyright (c) 2000 jbundle.org. All Rights Reserved.
 *		don@tourgeek.com
 */
package org.jbundle.terminal.model.demo;


import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import org.jbundle.jproperties.*;
import org.jbundle.terminal.*;
import org.jbundle.terminal.model.*;
import org.jbundle.terminal.model.datageneral.*;
import org.jbundle.terminal.model.remote.*;
import org.jbundle.terminal.view.*;


/**
 * Screen - This class contains all the data on a rowXcolumn screen.
 * <p>Basicly, the screen object receives characters on at a time from
 * the Terminal object, then notifies the ScreenView object of the
 * starting row and column that has been changed.
 * the characters (and their attributes) can also be accessed externally.
 * (ScreenView queries character cells when it needs to repaint a section
 * of its display)
 *<p>This base class just sticks each character in the current cursor location
 * and advances the cursor. At EOL, cursor is moved to the start of the next
 * line. At EOS, data is moved up (scrolled down).
 *<p>In the overriding classes, special characters must be captured and
 * interpreted.
 */
public class DemoModel extends DataGeneralModel
{
	private javax.swing.Timer m_timer = null;						// Timer for the blinking cursor.
	public static final String m_strCannedMessage =
		"" + kClearScreen +
		"         Welcome to the JTerminal Demo" + kCReturn + kNewLine +
		"         The characters that you are typing "  + kCReturn + kNewLine +
		"         are being echoed from my server in L.A." + kCReturn + kNewLine +
		"     " + kStartBlink + "A Data General D210 Terminal could do cool things like:" + kEndBlink + kCReturn + kNewLine +
		"     " + kPosCursor + kA17 + kA6 + "Position the cursor on the screen" + kCReturn + kNewLine +
		"     " + kStartUnderline + "Underline stuff" + kEndUnderline + kCReturn + kNewLine +
		"     " + kStartReverse + "Reverse video" + kEndReverse + kCReturn + kNewLine +
		"     " + kDimOn + "Dim characters" + kDimOff + kCReturn + kNewLine +
		"     " + kStartBlink + "And "+ kStartReverse + "mix" + kDimOn + " everything" + kEndReverse + " up" + kEndBlink + kDimOff + kCReturn + kNewLine +
		"For you non dgers, ^L (ctrl-L) clears the screen, Underline: ^t/^u" + kCReturn + kNewLine +
		"Have fun, your artwork will be reset after a minute of inactivity!" + kCReturn + kNewLine;
	/**
	 * Constructor.
	 */
	public DemoModel()
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public DemoModel(BaseView screenPanel,Properties properties)
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
		if (m_timer == null)
		{
			int TIME_MIN = 1;	// 60
			m_timer = new javax.swing.Timer(TIME_MIN * 1000 * 60, this);
			m_timer.setRepeats(false);
			m_timer.start();
		}
	}
	/**
	 * Free - Clean up.
	 */
	public void free()
	{
		if (m_timer != null)
		{
			m_timer.stop();
			m_timer = null;
		}
		super.free();
	}
	/**
	 * Process the timer event.
	 */
	public void actionPerformed(ActionEvent event)
	{
		if (m_timer != null)
			if (event.getSource() == m_timer)
		{
			for (int i = 0; i < m_strCannedMessage.length(); i++)
			{
				this.sendChar(m_strCannedMessage.charAt(i));
			}
			m_timer.stop();
		}
		super.actionPerformed(event);
	}
	/**
	 * Start the timer.
	 */
	public void sendChar(char chChar)
	{
		if (m_timer != null)
			m_timer.restart();		// Start the timer again
		super.sendChar(chChar);
	}
}
