/**
 * Copyright (c) 2000 jbundle.org. All Rights Reserved.
 *		don@tourgeek.com
 */
package org.jbundle.terminal.model;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;

import org.jbundle.util.apprunner.*;
import org.jbundle.terminal.*;
import org.jbundle.terminal.view.*;
/**
 * ScreenModel - This class contains all the data on a rowXcolumn screen.
 * <p>Basicly, the screen object receives characters on at a time from
 * the Terminal object, then notifies the ScreenView object of the
 * starting row and column that has been changed.
 * The characters (and their attributes) can also be accessed externally.
 * (ScreenView queries character cells when it needs to repaint a section
 * of its display)
 *<p>This base class just sticks each character in the current cursor location
 * and advances the cursor. At EOL, cursor is moved to the start of the next
 * line. At EOS, data is moved up (scrolled down).
 *<p>In the overriding classes, special characters must be captured and
 * interpreted by the "filter" method.
 */
public class ScreenModel extends Object
	implements ActionListener, BaseModel
{
	/**
	 * Screen panel.
	 */
	protected BaseView m_screenView = null;
	/**
	 * Physical screen data.
	 */
	protected char[][] m_chScreenMatrix = null;
	/**
	 * Attribute for each cell.
	 */
	protected short[][] m_rgsAttribute = null;
	/**
	 * Current attributes (for next character).
	 */
	protected short m_sCurrentAttribute = 0;
	/**
	 * Cursor current row and column.
	 */
	protected Point m_rcCurrent = new Point(0, 0);
	/**
	 * For multiple character sequences, number in the sequence.
	 */
	protected int m_iMultipleChar = 0;
	/**
	 * For multiple character sequences, first char in the sequence.
	 */
	protected char m_chMultipleChar = 0;
	/**
	 * Auto-scroll page up at bottom of screen.
	 * NOTE: This has nothing to do with the scoll bars, this flag tells
	 * the model whether to move all the text up when the EOP is reached.
	 */
	protected boolean m_bAutoScroll = true;
	
	/**
	 * Attributes
	 */
	public static final short NORMAL = 0;
	public static final short DIM = 1 << 0;
	public static final short BLINK = 1 << 1;		// This character blinks
	public static final short UNDERLINED = 1 << 3;
	public static final short REVERSED = 1 << 5;
	public static final short PROTECTED = 1 << 6;	// This character is not cleared on an erase page or line.

	public static final short BLINK_ON = 1 << 2;	// This character is currently blinked (invisible)
	public static final short CURSOR_OFF = 1 << 13;	// Attribute of a cursor off at this location
	public static final short CURSOR_ON = 1 << 14;	// Attribute of a cursor on at this location
	
	/**
	 * Should I blink the cursor?
	 */
	public boolean m_bBlinkCursor = true;	// Blink the cursor?
	protected boolean m_bCursorOn = true;					// Is the cursor currently on or off?
	private javax.swing.Timer m_timerCursor = null;						// Timer for the blinking cursor.
	
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";

	/**
	 * Should I blink the blinked characters?
	 */
	public boolean m_bBlinkChars = true;	// Blink the blinked characters?
	protected boolean m_bBlinkCharsOn = true;				// Are the characters currntly blinked or not?
	private javax.swing.Timer m_timerBlink = null;

	/**
	 * Constructor.
	 */
	public ScreenModel()
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public ScreenModel(BaseView screenPanel, Properties properties)
	{
		this();
		this.init(screenPanel, properties);
	}
	/**
	 * Set up the model.
	 */
	public void init(BaseView screenView, Properties properties)
	{
		this.setScreenView(screenView);
		this.setProperties(properties);		// Get the properties and set up this control
		this.clearScreen();
	}
	/**
	 * Clean up.
	 */
	public void free()
	{
		m_chScreenMatrix = null;
		if (m_timerCursor != null)
		{
			m_timerCursor.stop();
			m_timerCursor = null;
		}
		if (m_timerBlink != null)
		{
			m_timerBlink.stop();
			m_timerBlink = null;
		}
		if (m_screenView != null)
			if (m_screenView.getScreenControl() != null)
				if (m_screenView.getScreenControl().getScreenModel() == this)
					m_screenView.getScreenControl().setScreenModel(null);
		m_screenView = null;
	}
	/**
	 * Reset this control up to implement these new properties.
	 */
	public void setProperties(Properties properties)
	{
		int iWidth = 80;
		int iHeight = 24;
		if (properties != null)
		{
			String strWidth = properties.getProperty(WIDTH);
			if ((strWidth != null) && (strWidth.length() > 0))
			{
				try	{
					iWidth = Integer.parseInt(strWidth);
				} catch (NumberFormatException ex)	{
					iWidth = 80;
				}
			}
			String strHeight = properties.getProperty(HEIGHT);
			if ((strHeight != null) && (strHeight.length() > 0))
			{
				try	{
					iHeight = Integer.parseInt(strHeight);
				} catch (NumberFormatException ex)	{
					iHeight = 24;
				}
			}
		}
		if ((m_chScreenMatrix == null) ||
			(m_chScreenMatrix.length != iWidth) || (m_chScreenMatrix[0].length != iHeight))
		{
			m_chScreenMatrix = new char[iWidth][iHeight];	// Row 1 = Row 0
			m_rgsAttribute = new short[iWidth][iHeight];		// Attributes
			this.clearScreen();
		}

		String strBlink = null;
		if (properties != null)
			strBlink = properties.getProperty(ScreenPropertyView.CURSOR_BLINK_PARAM);
		m_bBlinkCursor = true;
		if ((strBlink != null) && (strBlink.length() > 0))
			if ((strBlink.charAt(0) == 'f')
			|| (strBlink.charAt(0) == 'F')
			|| (strBlink.charAt(0) == 'n')
			|| (strBlink.charAt(0) == 'N'))
				m_bBlinkCursor = false;
		if (m_bBlinkCursor)
		{
			if (m_timerCursor == null)
			{
				m_timerCursor = new javax.swing.Timer(500, this);
				m_timerCursor.setRepeats(true);
				m_timerCursor.start();
			}
		}
		else
		{
			if (m_timerCursor != null)
				m_timerCursor.stop();
			m_bCursorOn = false;	// Redisplay cursor
			m_timerCursor = null;
		}

		strBlink = null;
		if (properties != null)
			strBlink = properties.getProperty(ScreenPropertyView.CHAR_BLINK_PARAM);
		m_bBlinkChars = true;
		if ((strBlink != null) && (strBlink.length() > 0))
			if ((strBlink.charAt(0) == 'f')
			|| (strBlink.charAt(0) == 'F')
			|| (strBlink.charAt(0) == 'n')
			|| (strBlink.charAt(0) == 'N'))
				m_bBlinkChars = false;
		if (m_bBlinkChars)
		{
			if (m_timerBlink == null)
			{
				m_timerBlink = new javax.swing.Timer(750, this);
				m_timerBlink.setRepeats(true);
				m_timerBlink.start();
			}
		}
		else
		{
			if (m_timerBlink != null)
				m_timerBlink.stop();
			m_bBlinkCharsOn = false;	// Blinked characters - On.
			m_timerBlink = null;
		}
	}
	/**
	 * Process the timer event.
	 */
	public void actionPerformed(ActionEvent event)
	{
		if (m_timerCursor != null)
			if (event.getSource() == m_timerCursor)
		{
			m_bCursorOn = m_bCursorOn ? false : true;	// Toggle flag
			if (m_screenView != null)
				m_screenView.repaintChars(m_rcCurrent, m_rcCurrent);
		}
		if (m_timerBlink != null)
			if (event.getSource() == m_timerBlink)
		{
			m_bBlinkCharsOn = m_bBlinkCharsOn ? false : true;	// Toggle flag
			if (m_screenView != null)
			{
				int iWidth = this.getWidth();
				int iHeight = this.getHeight();
				boolean bCollectingBlinks = false;	// When collecting characters to invalidate
				for (int y = 0; y < iHeight; y++)
					for (int x = 0; x < iWidth; x++)
				{
					if ((m_rgsAttribute[x][y] & ScreenModel.BLINK) != 0)
					{	// This character needs to be blinked
						if (!bCollectingBlinks)
							ptStartChange.setLocation(x, y);	// Start
						ptEndChange.setLocation(x, y);	// End
						bCollectingBlinks = true;		// Start/continue collecting chars
						m_rgsAttribute[x][y] = (short)(m_rgsAttribute[x][y] & ~BLINK_ON);	// Clear bit
						if (m_bBlinkCharsOn)
							m_rgsAttribute[x][y] = (short)(m_rgsAttribute[x][y] | BLINK_ON);	// Set bit if blinked
					}
					else
					{
						if (bCollectingBlinks)
							m_screenView.repaintChars(ptStartChange, ptEndChange);	// Invalidate these locations, so characters will blink
						bCollectingBlinks = false;		// Stop collecting
					}
				}
				if (bCollectingBlinks)
					m_screenView.repaintChars(ptStartChange, ptEndChange);	// Invalidate these locations, so characters will blink
			}
		}
	}
	/**
	 * Get the width of the screen.
	 */
	public int getWidth()
	{
		return m_chScreenMatrix.length;
	}
	/**
	 * get the height of the screen.
	 */
	public int getHeight()
	{
		return m_chScreenMatrix[0].length;
	}
	/**
	 * Get the panel listener.
	 */
	public void setScreenView(BaseView screenView)
	{
		m_screenView = screenView;
	}
	/**
	 * Get the panel listener.
	 */
	public BaseView getScreenView()
	{
		return m_screenView;
	}
	/**
	 * Start/End change are utilitys to keep from re-allocating new Point(x,x).
	 */
	protected Point ptStartChange = new Point(0, 0);
	protected Point ptEndChange = new Point(0, 0);

	/**
	 * Add a character to the screen and move the character over by one
	 * then notify the ScreenView of the change.
	 * m_ptStartChange - Starting row/column on the screen that changed.
	 * m_ptEndChange - Ending row/column on the screen that changed.
	 */
	public void sendChar(char chChar)
	{
		if (m_iMultipleChar == 0)
			chChar = this.filter(chChar);
		else
			chChar = this.filterMultiple(chChar);	// Second character in a 2 character command
		if (chChar == 0)
			return;	// Non-printable
		m_chScreenMatrix[m_rcCurrent.x][m_rcCurrent.y] = chChar;
		m_rgsAttribute[m_rcCurrent.x][m_rcCurrent.y] = m_sCurrentAttribute;
		this.moveCursor(m_rcCurrent.x + 1, m_rcCurrent.y, true);
//x		m_screenPanel.repaintChars(m_rcCurrent, m_rcCurrent);	// Not necessary, since moveCursor calls
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
		case '\r':	//? Ctrl J - Move to start of line
			this.moveCursor(0, m_rcCurrent.y, true);
			chChar = 0;
			break;
		case '\n':	// Ctrl Z - Move down
			this.moveCursor(0, m_rcCurrent.y + 1, false);
			chChar = 0;
			break;
		default:
			break;
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
		m_iMultipleChar = 0;	// Reset control
		m_chMultipleChar = 0;
		return 0;
	}
	/**
	 * Exclusive or the bit set in BitToFlip.
	 * Note: there must be an easier way to exclusive or!
	 */
	public int flipBit(int iTarget, int iBitToFlip)
	{
		if ((iTarget & iBitToFlip) == 0)	// Is the target bit set?
			return iTarget | iBitToFlip;	// No, set it
		else
			return iTarget & ~iBitToFlip;	// Yes, clear it
	}
	/**
	 * Set or clear this attribute (All characters after this will have this attribute).
	 */
	public void changeAttribute(int iBitToFlip, boolean bClearOrSet)
	{
		if (bClearOrSet)	// Is the target bit set?
			m_sCurrentAttribute = (short)(m_sCurrentAttribute | iBitToFlip);	// No, set it
		else
			m_sCurrentAttribute = (short)(m_sCurrentAttribute & ~iBitToFlip);	// Yes, clear it
	}
		// These are the operations that can be done on the model:
	/**
	 * Clear the screen to spaces.
	 */
	public void clearScreen()
	{
		int iWidth = this.getWidth();
		int iHeight = this.getHeight();
		for (int y = 0; y < iHeight; y++)
			for (int x = 0; x < iWidth; x++)
		{
			m_chScreenMatrix[x][y] = ' ';	// Clear the screen area
			m_rgsAttribute[x][y] = 0;		// No attribute
		}
		m_sCurrentAttribute = 0;	// Clear current attribute
		this.moveCursor(0, 0, false);
		ptStartChange.setLocation(0, 0);
		ptEndChange.setLocation(this.getWidth() - 1, this.getHeight() - 1);
		if (m_screenView != null)
			m_screenView.repaintChars(ptStartChange, ptEndChange);
	}
	/**
	 * Move the screen cursor to this new location.
	 * @param bAutoReturn If set, moving to the end of the line will move to the next line (as well as the first position).
	 */
	public void moveCursor(int x, int y, boolean bAutoReturn)
	{
//		if ((m_rcCurrent.x + 1 != x) || (m_rcCurrent.y != y))
//			m_sCurrentAttribute = 0;	// No current attribute after a move cursor anywhere but next
		ptStartChange.setLocation(m_rcCurrent.x, m_rcCurrent.y);
		ptEndChange.setLocation(m_rcCurrent.x, m_rcCurrent.y);
		m_rcCurrent.setLocation(x, y);
		if (m_screenView != null)
			m_screenView.repaintChars(ptStartChange, ptEndChange);	// Old cursor location
		if (m_rcCurrent.x >= this.getWidth())
		{
			m_rcCurrent.x = 0;
			if (bAutoReturn)
				m_rcCurrent.y++;
		}
		else if (m_rcCurrent.x < 0)
			m_rcCurrent.x = this.getWidth() - 1;
		if (m_rcCurrent.y >= this.getHeight())
		{
			if ((bAutoReturn) && (m_bAutoScroll))
				this.scrollUp();	// Cursor is automatically adjusted
			else
				m_rcCurrent.y = 0;
		}
		else if (m_rcCurrent.y < 0)
			m_rcCurrent.y = this.getHeight() - 1;
		if (m_screenView != null)
			m_screenView.repaintChars(m_rcCurrent, m_rcCurrent);
	}
	/**
	 * Scroll all the data up by one line.
	 */
	public void scrollUp()
	{
		int iWidth = this.getWidth();
		int iHeight = this.getHeight();
		for (int y = 0; y < iHeight - 1; y++)
			for (int x = 0; x < iWidth; x++)
		{
			{
				m_chScreenMatrix[x][y] = m_chScreenMatrix[x][y+1];
				m_rgsAttribute[x][y] = m_rgsAttribute[x][y+1];
			}
//?			System.arraycopy(m_chScreenMatrix[y+1], src_position, m_chScreenMatrix[y], dst_position, iLength);
		}
		for (int x = 0; x < iWidth; x++)
		{
			m_chScreenMatrix[x][iHeight-1] = ' ';	// Clear the last line
			m_rgsAttribute[x][iHeight-1] = 0;		// No attribute
		}
		ptStartChange.setLocation(0, 0);
		ptEndChange.setLocation(this.getWidth() - 1, this.getHeight() - 1);
		if (m_screenView != null)
			m_screenView.repaintChars(ptStartChange, ptEndChange);
		this.moveCursor(m_rcCurrent.x, m_rcCurrent.y - 1, false);
	}
	/**
	 * Get the character at this location.
	 */
	public char getChar(int x, int y)
	{
		try	{
			return m_chScreenMatrix[x][y];
		} catch (ArrayIndexOutOfBoundsException ex)	{
			return 0;
		}
	}
	/**
	 * Get the attributes at this location.
	 */
	public short getAttributes(int x, int y)
	{
		short shAttribute = 0;
		try	{
			shAttribute = m_rgsAttribute[x][y];
		} catch (ArrayIndexOutOfBoundsException ex)	{
			shAttribute = 0;
		}
		if ((m_rcCurrent.x == x) && (m_rcCurrent.y == y))
		{		// this is the cursor position
			if (m_bCursorOn)
				shAttribute |= CURSOR_OFF;
			else
				shAttribute |= CURSOR_ON;
		}
		return shAttribute;
	}
	/**
	 * Create a listener for this control.
	 */
	public ScreenKeyHandler createKeyListener(BaseControl screenControl)
	{
	    return new ScreenKeyHandler(screenControl);	// Override this
	}
	/**
	 * Screen that is used to change the properties.
	 */
	public PropertyView getPropertyView(Properties properties)
	{
		return new ScreenPropertyView(this, properties);
	}
	/**
	 * Physical screen data.
	 */
	public char[][] getCharacters()
	{
		return m_chScreenMatrix;
	}
	/**
	 * Attribute for each cell.
	 */
	public short[][] getAttributes()
	{
		return m_rgsAttribute;
	}
	/**
	 * Physical screen data.
	 */
	public void setCharacters(char[][] chScreenMatrix)
	{
		m_chScreenMatrix = chScreenMatrix;
	}
	/**
	 * Attribute for each cell.
	 */
	public void setAttributes(short[][] rgsAttribute)
	{
		m_rgsAttribute = rgsAttribute;
	}
}