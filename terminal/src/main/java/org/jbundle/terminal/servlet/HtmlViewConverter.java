/**
 * Copyright (c) 2000 jbundle.org. All Rights Reserved.
 *		Don_Corley@msn.com
 */
package org.jbundle.terminal.servlet;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import org.jbundle.jproperties.*;
import org.jbundle.terminal.*;
import org.jbundle.terminal.control.*;
import org.jbundle.terminal.control.serial.*;
import org.jbundle.terminal.model.*;
import org.jbundle.terminal.view.*;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import javax.comm.*;

/**
 * This is a utility class that is used to convert the model to HTML.
 */
public class HtmlViewConverter extends Object
{
	public ScreenModel m_screenModel = null;

	public static final String RETURN = "\n";
	
	public int m_iSpanCount = 0;

	/**
	 * Constructor.
	 */
	public HtmlViewConverter()
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public HtmlViewConverter(ScreenModel screenModel)
	{
		this();
		this.init(screenModel);
	}
	/**
	 * Constructor.
	 */
	public void init(ScreenModel screenModel)
	{
		this.setModel(screenModel);
	}
	/**
	 * Constructor.
	 */
	public void setModel(ScreenModel screenModel)
	{
		m_screenModel = screenModel;
	}
	/**
	 * Free the resources.
	 */
	public void free()
	{
		m_screenModel = null;
	}
	/**
	 * Send the screen out.
	 * @param out The output file.
	 */
	public void htmlScreenOut(PrintWriter out)
	{
		Writer out2 = new StringWriter();
		try	{
		out2.write("<html>" + RETURN +
						"<head>" + RETURN +
						"<title>DG Screen</title>" + RETURN +
						"</head>" + RETURN +
						"<body>" + RETURN +
						"DG Screen<p>" + RETURN +
						"<STYLE TYPE=\"text/css\">" + RETURN +
		"PRE { background-color: #DDDDDD; color: black }" + RETURN +
		"SPAN.NORMAL { background-color: #DDDDDD }" + RETURN +
		"SPAN.DIM { color: #8888FF }" + RETURN +
		"SPAN.BLINK { text-decoration: blink; color: red }" + RETURN +
		"SPAN.UNDERLINED { text-decoration: underline }" + RETURN +
		"SPAN.REVERSED { background-color: black; color: #DDDDDD }" + RETURN +
		"SPAN.CURSOR { background-color: black; color: #DDDDDD }" + RETURN +
		"</STYLE>" + RETURN +
		"<pre>" + RETURN);
		this.getHtmlScreenOut(out2);
		out2.write("" +
						"</pre>" + RETURN +
						"</body>" + RETURN +
						"</html>" + RETURN);
		} catch (IOException ex)	{
			ex.printStackTrace();
		}
		String strScreenOut = out2.toString();
		out.print(strScreenOut);
	}
	/**
	 * Create the html string from the model.
	 */
	public String getHtmlScreenOut()
	{
		Writer out2 = new StringWriter();
		this.getHtmlScreenOut(out2);
		return out2.toString();
	}
	/**
	 * Create the HTML screen from the model.
	 */
	public void getHtmlScreenOut(Writer out)
	{
		ScreenModel screenModel = m_screenModel;//(ScreenModel)m_screenView.getScreenControl().getScreenModel();
		int iWidth = screenModel.getWidth();
		int iHeight = screenModel.getHeight();
		for (int y = 0; y < iHeight; y++)
		{
			int iOldAttributes = 0;
			for (int x = 0; x < iWidth; x++)
			{
				char chData = screenModel.getChar(x, y);	// Get the char at this location
				int iAttributes = screenModel.getAttributes(x, y);	// Get the attributes of this cell
				if (chData == 0)
					chData = ' ';
				this.drawThisChar(out, chData, iOldAttributes, iAttributes);
				iOldAttributes = iAttributes;
			}
			this.drawThisChar(out, (char)-1, iOldAttributes, 0);	// End of line
		}
	}
	/**
	 * Draw this character with these attributes in this rectangle in the graphics env.
	 */
	public void drawThisChar(Writer out, char chData, int iOldAttributes, int iAttributes)
	{
		this.postDrawAttributes(out, iOldAttributes, iAttributes);

		try	{
			if (chData == (char)-1)
			{	// End of line
				out.write(RETURN);
				return;
			}
			this.preDrawAttributes(out, iOldAttributes, iAttributes);

			String strSpecial = null;
			if (chData == '<')
				strSpecial = "&lt;";
			if (chData == '>')
				strSpecial = "&gt;";
			if (chData == '&')
				strSpecial = "&amp;";
			if (chData == '\"')
				strSpecial = "&quot;";
			if (strSpecial == null)
				out.write(chData);
			else
				out.write(strSpecial);
		} catch (IOException ex)	{
			ex.printStackTrace();
		}
	}
	/**
	 * Output the end tag(s) to disable these attributes.
	 */
	public void preDrawAttributes(Writer out, int iOldAttributes, int iAttributes)
	{
		try	{
			if (this.checkAttribute(iAttributes, ScreenModel.DIM))
			{
				out.write("<span class=DIM>");
				m_iSpanCount++;
			}
			if (this.checkAttribute(iAttributes, ScreenModel.BLINK))
			{
				out.write("<span class=BLINK>");
				m_iSpanCount++;
			}
			if (this.checkAttribute(iAttributes, ScreenModel.UNDERLINED))
			{
				out.write("<span class=UNDERLINED>");
				m_iSpanCount++;
			}
			if (this.checkAttribute(iAttributes, ScreenModel.REVERSED))
			{
				out.write("<span class=REVERSED>");
				m_iSpanCount++;
			}
			if ((this.checkAttribute(iAttributes, ScreenModel.CURSOR_ON))
				|| (this.checkAttribute(iAttributes, ScreenModel.CURSOR_OFF)))
			{
				out.write("<span class=CURSOR>");
				m_iSpanCount++;
			}
		} catch (IOException ex)	{
				ex.printStackTrace();
		}
	}
	/**
	 * Output the start tag(s) to enable these attributes.
	 */
	public void postDrawAttributes(Writer out, int iOldAttributes, int iAttributes)
	{
		if (iOldAttributes != iAttributes)
		{
			try	{
				for (; m_iSpanCount > 0; m_iSpanCount--)
				{
					out.write("</span>");
				}
			} catch (IOException ex)	{
				ex.printStackTrace();
			}
		}
	}
	/**
	 * Output the start tag(s) to enable these attributes.
	 */
	public boolean checkAttribute(int iAttributes, int iBitToCheck)
	{
		if ((iAttributes & iBitToCheck) != 0)
			return true;	// Bit turned on
		return false;	// Bit not changed or not turned the correct way
	}
}
