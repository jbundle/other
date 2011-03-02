/**
 * Copyright (c) 2000 jbundle.org. All Rights Reserved.
 *		Don_Corley@msn.com
 */
package org.jbundle.terminal.control.loopback;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

import org.jbundle.util.apprunner.*;
import org.jbundle.terminal.*;
import org.jbundle.terminal.control.*;
import org.jbundle.terminal.control.remote.*;

/**
 * LoopbackControl - For testing only.
 * Sends the character directly to the model.
 */
public class LoopbackControl extends RemoteControl
	implements BaseControl
{
	protected boolean m_bPrintChars = false;

	/**
	 * Constructor.
	 */
	public LoopbackControl()
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public LoopbackControl(BaseModel screenModel, Properties properties)
	{
		this();
		this.init(screenModel, properties);
	}
	/**
	 * Constructor.
	 */
	public void init(BaseModel screenModel, Properties properties)
	{
		super.init(screenModel, properties);
	}
	/**
	 * Get the properties and set up this control.
	 */
	public void setProperties(Properties properties)
	{
		String strPrint = null;
		if (properties != null)
			strPrint = properties.getProperty(LoopbackPropertyView.PRINT_PARAM);
		m_bPrintChars = false;
		if ((strPrint != null) && (strPrint.length() > 0))
			if ((strPrint.charAt(0) == 't')
			|| (strPrint.charAt(0) == 'T')
			|| (strPrint.charAt(0) == 'y')
			|| (strPrint.charAt(0) == 'Y'))
				m_bPrintChars = true;
		super.setProperties(properties);
	}
	/**
	 * Send this character to the control.
	 * Send it back to the receiver.
	 */
	public void sendCharToControl(char chChar)
	{
		super.sendCharToControl(chChar);
		if (m_bPrintChars)
		{
			String strOut = " ";
			if (Character.isLetterOrDigit(chChar))
				strOut = "" + chChar;
			strOut += " " + Integer.toString(chChar);
			System.out.println(strOut);
		}
		this.sendCharToModel(chChar);
	}
	/**
	 * Screen that is used to change the properties.
	 */
	public PropertyView getPropertyView(Properties properties)
	{
		return new LoopbackPropertyView(this, properties);
	}
}
