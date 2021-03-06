/*
 *		don@tourgeek.com
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.terminal.model.datageneral;


import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import org.jbundle.util.apprunner.*;
import org.jbundle.terminal.*;
import org.jbundle.terminal.model.*;
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
public class DataGeneralModel extends RemoteModel
	implements DataGeneralConst	// Constants
{

	/**
	 * Constructor.
	 */
	public DataGeneralModel()
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public DataGeneralModel(BaseView screenPanel, Properties properties)
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
	 * Convert this character to the actual representation.
	 * (Override this to process any special characters)
	 */
	public char filter(char chChar)
	{
		if (Character.isLetterOrDigit(chChar))
			return chChar;
		switch (chChar)
		{
		case NULL:	// null - Ignore
			chChar = 0;	// Ignore
			break;
		case A1:	// Ctrl A
			chChar = 0;
			break;
		case END_REVERSE:	// Ctrl B
			this.changeAttribute(REVERSED, false);
			chChar = 0;
			break;
		case A3:	// Ctrl C
			chChar = 0;
			break;
		case A4:	// Ctrl D
			chChar = 0;
			break;
		case READ_CUR_POS:	// Ctrl E
			BaseControl screenControl = this.getScreenView().getScreenControl();
			screenControl.sendCharToControl(CUR_POS_HDR);
			screenControl.sendCharToControl((char)m_rcCurrent.x);	// Column
			screenControl.sendCharToControl((char)m_rcCurrent.y);	// Row
			chChar = 0;
			break;
		case A6:		// Ctrl F
			chChar = 0;
			break;
		case BELL:		// Ctrl G - Ring the bell
			Toolkit.getDefaultToolkit().beep();
			chChar = 0;
			break;
		case HOME:		// Ctrl H - Move home
			this.moveCursor(0, 0, false);
			chChar = 0;
			break;
		case TAB:		// Ctrl I
			chChar = 0;	// Ignore
			break;
		case NEW_LINE:	//? Ctrl J - Move to start of next line
			this.moveCursor(0, m_rcCurrent.y + 1, true);
			chChar = 0;
			break;
		case ERASE_EOL:	// Ctrl K
			this.clearToEol();
			chChar = 0;
			break;
		case CLEAR_SCREEN:	// Ctrl L - Clear screen
			this.clearScreen();
			chChar = 0;
			break;
		case C_RETURN:	// Ctrl M
			this.moveCursor(0, m_rcCurrent.y, true);
			chChar = 0;
			break;
		case START_BLINK:	// Ctrl N
			this.changeAttribute(BLINK, true);
			chChar = 0;
			break;
		case END_BLINK:	// Ctrl O
			this.changeAttribute(BLINK, false);
			chChar = 0;
			break;
		case POS_CURSOR:	// Ctrl P
			m_iMultipleChar++;	// First char of sequence
			m_chMultipleChar = chChar;
			chChar = 0;
			break;
		case A17:	// Ctrl Q
			chChar = 0;
			break;
		case PAGE_OFF:	// Ctrl R
			m_bAutoScroll = true;
			chChar = 0;
			break;
		case PAGE_ON:	// Ctrl S
			m_bAutoScroll = false;
			chChar = 0;
			break;
		case START_UNDERLINE:	// Ctrl T
			this.changeAttribute(UNDERLINED, true);
			chChar = 0;
			break;
		case END_UNDERLINE:	// Ctrl U
			this.changeAttribute(UNDERLINED, false);
			chChar = 0;
			break;
		case START_REVERSE:	// Ctrl V
			this.changeAttribute(REVERSED, true);
			chChar = 0;
			break;
		case UP:	// '\027':	// Ctrl W - Move up
			this.moveCursor(m_rcCurrent.x, m_rcCurrent.y - 1, false);
			chChar = 0;
			break;
		case RIGHT:	// '\030':	// Ctrl X - Move right
			this.moveCursor(m_rcCurrent.x + 1, m_rcCurrent.y, false);
			chChar = 0;
			break;
		case LEFT:	// '\031':	// Ctrl Y - Move left
			this.moveCursor(m_rcCurrent.x - 1, m_rcCurrent.y, false);
			chChar = 0;
			break;
		case DOWN:	// '\032':	// Ctrl Z - Move down
			this.moveCursor(m_rcCurrent.x, m_rcCurrent.y + 1, false);
			chChar = 0;
			break;
		case A27:
			chChar = 0;
			break;
		case DIM_ON:
			this.changeAttribute(DIM, true);
			chChar = 0;
			break;
		case DIM_OFF:
			this.changeAttribute(DIM, false);
			chChar = 0;
			break;
		case TERM_ID1:
			m_iMultipleChar++;	// First char of sequence
			m_chMultipleChar = chChar;
			chChar = 0;
			break;
		default:
			return super.filter(chChar);
//			break;
		}
		return chChar;
	}
	/**
	 * For multiple character sequences, process this char.
	 * Override this to process the char and
	 * remember to bump iMultipleChar and set chMultipleChar.
	 */
	public char filterMultiple(char chChar)
	{
		switch (m_chMultipleChar)
		{
			case POS_CURSOR:	// Ctrl P
				if (m_iMultipleChar == 1)
				{
					this.moveCursor(chChar, m_rcCurrent.y, false);
					m_iMultipleChar++;	// First char of sequence
					return 0;	// Keep going
				}
				// else if (m_iMultipleChar == 2)
					this.moveCursor(m_rcCurrent.x, chChar, false);
				break;		// Done, reset multiple
			case TERM_ID1:
				if (m_iMultipleChar == 1)
				{	// Second character in sequence
					if (chChar == TERM_ID2)
					{	// Asking for the terminal ID
						BaseControl screenControl = this.getScreenView().getScreenControl();
						screenControl.sendCharToControl(TERM_ID_HDR);
						screenControl.sendCharToControl('o');
						screenControl.sendCharToControl('#');
						screenControl.sendCharToControl('1');
						screenControl.sendCharToControl('T');
						screenControl.sendCharToControl('y');
					}
					else if (chChar == REVERSE_ON_2)
						this.changeAttribute(REVERSED, true);
					else if (chChar == REVERSE_OFF_2)
						this.changeAttribute(REVERSED, false);
				}
				break;
			default:
		}
		return super.filterMultiple(chChar);	// This resets everything
	}
	/**
	 * Clear the screen to spaces.
	 */
	public void clearToEol()
	{
		int iWidth = this.getWidth();
		for (int x = m_rcCurrent.x; x < iWidth; x++)
		{
			m_chScreenMatrix[x][m_rcCurrent.y] = ' ';	// Clear the screen area
			m_rgsAttribute[x][m_rcCurrent.y] = 0;		// No attribute
		}
		if (m_screenView != null)
			m_screenView.repaintChars(m_rcCurrent, new Point(iWidth - 1, m_rcCurrent.y));		// Repaint all
//		m_sCurrentAttribute = 0;
	}
	/**
	 * Create a listener for this control.
	 */
	public ScreenKeyHandler createKeyListener(BaseControl screenControl)
	{
	    return new DataGeneralKeyHandler(screenControl);
	}
	/**
	 * Screen that is used to change the properties.
	 */
	public PropertyView getPropertyView(Properties properties)
	{
		return new DataGeneralPropertyView(this, properties);
	}
}
