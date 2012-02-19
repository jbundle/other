/*
 *		don@tourgeek.com
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.terminal.view.monitor;

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
import org.jbundle.terminal.view.remote.*;

/**
 * MonitorView - The physical display screen.
 */
public class MonitorView extends RemoteView
	implements BaseView
{
	protected char m_data[];

	protected Dimension m_dimBox = new Dimension(0, 0);
	protected int m_iBaselineOffset = 0;

	protected Color m_colorBackground = Color.white;
	protected Color m_colorCharacters = Color.blue;
	protected Color m_colorDim = Color.gray;
	protected Color m_colorBold = Color.black;
	protected Color m_colorUnderline = m_colorCharacters;

	protected Color m_colorCursor = Color.darkGray;
	
	protected int m_iFontSize = 12;
	
	/**
	 * Temporary rectangle used to keep from continually allocating on the heap.
	 */
	private Rectangle m_rectTemp = new Rectangle(0, 0, 0, 0);

	/**
	 * Constructor.
	 */
	public MonitorView()
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public MonitorView(BaseControl screenControl, Properties properties)
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

		javax.swing.FocusManager.disableSwingFocusManager();	// Don't let system have tabs
		
		m_data = new char[1];
		this.requestFocus();
	}
	/**
	 * Free the resources.
	 */
	public void free()
	{
		 super.free();
	}
	/**
	 * Constructor.
	 */
	public Dimension getMinimumSize()
	{
		return this.getPreferredSize();
	}
	/**
	 * Constructor.
	 */
	public Dimension getPreferredSize()
	{
		ScreenModel screenModel = (ScreenModel)this.getScreenControl().getScreenModel();
		return new Dimension(screenModel.getWidth() * m_dimBox.width, screenModel.getHeight() * m_dimBox.height);
	}
	/**
	 * Calculate the width and height of a character block.
	 */
	public void calcFontStuff()
	{
		this.setFont(new Font("Monospaced", Font.PLAIN, m_iFontSize));
		Font font = this.getFont();
		if (font == null)
			return;
		FontMetrics fm = this.getFontMetrics(font);
		m_dimBox.height = fm.getHeight();
		m_dimBox.width = fm.charWidth('W');
		m_iBaselineOffset = fm.getLeading() + fm.getAscent();
	}
	/**
	 * Repaint the screen for this starting and ending locations.
	 * <p>Note: There may be small problem with syncronization it two timers
	 * call this at the same time from different tasks (since I use the
	 * shared m_rectTemp), but the resulting
	 * outcome should only cause a quick visual hickup which will be corrected
	 * on the next timer click.
	 */
	public void repaintChars(Point ptStartChange, Point ptEndChange)
	{
		this.convertRCtoCell(ptStartChange, m_rectTemp);
		int iTop = m_rectTemp.y;
		int iLeft = m_rectTemp.x;
		this.convertRCtoCell(ptEndChange, m_rectTemp);
		int iBottom = m_rectTemp.y + m_rectTemp.height;
		int iRight = m_rectTemp.x + m_rectTemp.width;
		if (ptStartChange.y < ptEndChange.y)
		{		// If area spans multiple lines, invalidate intermediate lines.
			iLeft = 0;
			iRight = this.getBounds().width;	// Right side
		}
		this.repaint(iLeft, iTop, iRight - iLeft, iBottom - iTop);
	}

	protected Point m_rcStart = new Point(0, 0);
	protected Point m_rcEnd = new Point(0, 0);
	protected Point m_rcIndex = new Point(0, 0);
	protected Point m_ptStartPosition = new Point(0, 0);
	protected Point m_ptEndPosition = new Point(0, 0);
	/**
	 * Overidden to paint this area.
	 */
	public void paint(Graphics  g)
	{
		int iAttributes = -1;
		int iCurrentAttributes;
		g.setFont(this.getFont());		// Make sure I'm writing the correct font
		Rectangle rect = g.getClipBounds();	// Rectangle to render
		Color color = g.getColor();
		g.setColor(m_colorBackground);
		g.fillRect(rect.x, rect.y, rect.width, rect.height);
		m_ptStartPosition.x = rect.x;
		m_ptStartPosition.y = rect.y;
		this.convertPointtoRC(m_ptStartPosition, m_rcStart);
		m_ptEndPosition.x = rect.x + rect.width - 1;
		m_ptEndPosition.y = rect.y + rect.height - 1;
		this.convertPointtoRC(m_ptEndPosition, m_rcEnd);
		ScreenModel screenModel = (ScreenModel)this.getScreenControl().getScreenModel();
		for (m_rcIndex.y = m_rcStart.y; m_rcIndex.y <= m_rcEnd.y; m_rcIndex.y++)
			for (m_rcIndex.x = m_rcStart.x; m_rcIndex.x <= m_rcEnd.x; m_rcIndex.x++)
		{
			char chData = screenModel.getChar(m_rcIndex.x, m_rcIndex.y);	// Get the char at this location
			iAttributes = screenModel.getAttributes(m_rcIndex.x, m_rcIndex.y);	// Get the attributes of this cell
			if (chData != 0)
			{
				this.convertRCtoCell(m_rcIndex, m_rectTemp);
				this.drawThisChar(g, m_rectTemp, chData, iAttributes);
			}
		}
		g.setColor(color);

		this.requestFocus();		// HACK - Move this somewhere else!!!
	}
	/**
	 * Draw this character with these attributes in this rectangle in the graphics env.
	 */
	public void drawThisChar(Graphics g, Rectangle rectCell, char chData, int iAttributes)
	{
		m_data[0] = chData;
		this.preDrawAttributes(g, rectCell, iAttributes);
		g.drawChars(m_data, 0, 1, rectCell.x, rectCell.y + m_iBaselineOffset);
		this.postDrawAttributes(g, rectCell, iAttributes);
	}
	/**
	 * Set up this cell for these attributes.
	 */
	public void preDrawAttributes(Graphics g, Rectangle rectCell, int iAttributes)
	{
		Color colorBackground = m_colorBackground;
		g.setColor(m_colorCharacters);
		if (iAttributes != 0)
		{
			if ((iAttributes & ScreenModel.DIM) != 0)
			{
				g.setColor(m_colorDim);
			}
			if ((iAttributes & ScreenModel.REVERSED) != 0)
			{
				g.setColor(m_colorCharacters);
				g.fillRect(rectCell.x, rectCell.y, rectCell.width, rectCell.height);
				colorBackground = m_colorCharacters;
				g.setColor(m_colorBackground);
			}
			if ((iAttributes & ScreenModel.CURSOR_ON) != 0)
			{
				g.setColor(m_colorCursor);
				g.fillRect(rectCell.x, rectCell.y, rectCell.width, rectCell.height);
				colorBackground = m_colorCursor;
				g.setColor(m_colorBackground);	// Draw characters this color
			}
/*			if (!ScreenModel.m_bBlinkChars)
				if ((iAttributes & ScreenModel.BLINK) != 0)
			{		// If you arn't fast enough to handle blink...
				g.setColor(m_colorBold);
			}
			if (ScreenModel.m_bBlinkChars)
				if ((iAttributes & ScreenModel.BLINK_ON) != 0)
			{		// Draw characters same color as background = can't see
				g.setColor(colorBackground);
			}
*/		}
	}
	/**
	 * Draw this character with these attributes in this rectangle in the graphics env.
	 */
	public void postDrawAttributes(Graphics g, Rectangle rectCell, int iAttributes)
	{
		if (iAttributes != 0)
		{
			if ((iAttributes & ScreenModel.UNDERLINED) != 0)
			{
				g.setColor(m_colorUnderline);
				g.drawLine(rectCell.x, rectCell.y + m_iBaselineOffset, rectCell.x + rectCell.width, rectCell.y + m_iBaselineOffset);
			}
		}
	}
	/**
	 * ConvertPointtoRC - Convert this Point to a row/column.
	 * NOTE: This method returns the Point passed in which is not good practice,
	 *	unless in this case, I know that rcPoint is never used again.
	 */
	public void convertPointtoRC(Point ptPoint, Point rcPoint)
	{
		rcPoint.x = (m_dimBox.width == 0 ? 0 : ptPoint.x / m_dimBox.width);
		rcPoint.y = (m_dimBox.height == 0 ? 0 : ptPoint.y / m_dimBox.height);
	}
	/**
	 * Convert this Row and Column position to screen cell.
	 */
	public void convertRCtoCell(Point rcPoint, Rectangle rectCell)
	{
		rectCell.x = rcPoint.x * m_dimBox.width;
		rectCell.width = m_dimBox.width;
		rectCell.y = rcPoint.y * m_dimBox.height;
		rectCell.height = m_dimBox.height;
	}
	/**
	 * Screen that is used to change the properties.
	 */
	public PropertyView getPropertyView(Properties properties)
	{
		return new MonitorPropertyView(this, properties);
	}
	/**
	 * Reset this control up to implement these new properties.
	 */
	public void setProperties(Properties properties)
	{
		String strCurrent = properties.getProperty(MonitorPropertyView.FONT_SIZE_PARAM);
		if ((strCurrent == null) || (strCurrent.length() == 0))
			strCurrent = "12";
		int iFontSize = 12;
		try	{
			iFontSize = Integer.parseInt(strCurrent);
		} catch (NumberFormatException ex)	{
			iFontSize = 12;
		}
		if ((m_dimBox.height == 0)
			|| (m_iFontSize != iFontSize))
		{
			m_iFontSize = iFontSize;
			this.calcFontStuff();
		}
		super.setProperties(properties);
	}
}
