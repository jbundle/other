package org.jbundle.terminal.rc;

/**
 * @(#)RemoteControlThread.java	0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2000 jbundle.org. All Rights Reserved.
 *		don@tourgeek.com
 */

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.jbundle.terminal.*;
import org.jbundle.terminal.control.*;
import org.jbundle.terminal.model.*;
import org.jbundle.terminal.server.*;

import java.rmi.*;
import java.awt.event.*;
import java.awt.*;

/**
 * RemoteControlThread
 * 
 * This is the base remote control application.
 */
public class RemoteControlThread extends Thread
	implements TerminalConstants
{
	protected BaseModel m_model = null;
	
	protected Date m_dateUp = new Date();
	protected Date m_dateConnectionLost = null;

	public final static long DOWN_MINUTES_REBOOT = 16 * 60;	// 20 hours Reboot after I'm down for this long
	public final static String REBOOT_COMMAND = "/BOOT *,Y";

	public final static int HOUR_INITIAL = 0;	// Initial wait time
	public final static int MINUTE_INITIAL = 10;	// Wait 10 minutes before first check"

	public final static int HOUR_INTERVAL = 6;	// Interval wait time between checks
	public final static int MINUTE_INTERVAL = 0;	// "

	/**
	 * Constructor
	 */
	public RemoteControlThread()
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public RemoteControlThread(BaseModel model)
	{
		this();
		m_model = model;
	}
    /**
     * Called to start the applet.  You never need to call this directly; it
     * is called when the applet's document is visited.
     */
    public void run()
    {
		super.run();
		System.out.println("Running");
		this.waitTime(HOUR_INITIAL, MINUTE_INITIAL, 0);
		byte[] rgByte = new byte[1000];
		while (true)
		{	// Keep looping
			URL url = null;
			boolean bError = false;
			try	{
				url = new URL("http://www.zyan.com");
//			} catch (MalformedURLException ex)	{
			} catch (Exception ex)	{
				ex.printStackTrace();
				bError = true;
			}
			try	{
				InputStream streamIn = url.openStream();
				streamIn.read(rgByte, 0 , rgByte.length);
				String strNew = new String(rgByte);
//x				System.out.println(strNew);	//x
				streamIn.close();
				streamIn = null;
//			} catch (IOException ex)	{
			} catch (Exception ex)	{
				ex.printStackTrace();
				bError = true;
			}
			if (!bError)
			{		// No errors, wait for a while and continue
				m_dateConnectionLost = null;		// Reset, connection is fine
				long lMinConnectionLost = this.msToMinutes(new Date().getTime() - m_dateUp.getTime());
				System.out.println("Connection up for " + lMinConnectionLost + " minutes");
			}
			else
			{		// There is an error!!!
				if (m_dateConnectionLost == null)
					m_dateConnectionLost = new Date();		// Date lost = now
				long lMinConnectionLost = this.msToMinutes(new Date().getTime() - m_dateConnectionLost.getTime());
				System.out.println("Connection down for " + lMinConnectionLost + " minutes");
				if (lMinConnectionLost >= DOWN_MINUTES_REBOOT)
				{
					System.out.println("Rebooting system");
					this.getThePrompt();
					this.sendCommand(REBOOT_COMMAND);
				}
			}
			this.waitTime(HOUR_INTERVAL, MINUTE_INTERVAL, 0);	// Wait until next time
		}
    }
	/**
	 * Get the command prompt
	 */
	public boolean getThePrompt()
	{
		while (true)
		{	// Keep trying
			this.sendCommand("");
			this.waitSec(3);
			if (this.waitForPrompt("NPS>", 10) == true)
			{
				return true;
			}
		}
	}
	/**
	 * Wait for this prompt.
	 */
	public boolean waitForPrompt(String strPrompt, int iWaitSec)
	{
		char[][] rgcharModel = ((ScreenModel)m_model).getCharacters();
		String[] rgString = this.charsToStrings(rgcharModel);
//x		for (int i = 0; i < rgString.length; i++)
//x			System.out.println(rgString[i]);
		if (iWaitSec <= 0)
			iWaitSec = 0;		// Minimun
		while (iWaitSec >= 0)
		{
			for (int i = rgString.length - 1; i >= 0; i--)
			{
				if (!this.isBlankLine(rgString[i]))
				{
					if (rgString[i].indexOf(strPrompt) == 0)
						return true;
				}
			}
			if (iWaitSec <= 0)
				break;		// Done
			int iWaitTime = iWaitSec;
			if (iWaitTime > 5)
				iWaitTime = 5;		// Max wait unit
			iWaitSec = iWaitSec - iWaitTime;
			this.waitSec(iWaitSec);
		}
		return false;
	}
	/**
	 * Send this command (followed by a carriage return).
	 */
	public void sendCommand(String strCommand)
	{
		ScreenControl control = (ScreenControl)m_model.getScreenView().getScreenControl();
		for (int i = 0; i <= strCommand.length(); i++)
		{
			char chKey = '\r';
			if (i < strCommand.length())
				chKey = strCommand.charAt(i);
			control.sendCharToControl(chKey);	// Send this character to the screen
		}
	}
	/**
	 * Convert this char matrix to a String array.
	 */
	public String[] charsToStrings(char[][] rgchars)
	{
		int iWidth = rgchars.length;
		String[] rgString = new String[rgchars[0].length];
		StringBuffer sb = new StringBuffer(iWidth);
		for (int r = 0; r < rgchars[0].length; r++)
		{
			sb.setLength(0);
			for (int c = 0; c < iWidth; c++)
			{
				sb.append(rgchars[c][r]);
			}
			rgString[r] = sb.toString();
		}
		return rgString;
	}
	/**
	 * Is this a blank line?
	 */
	public boolean isBlankLine(String strLine)
	{
		for (int i = 0; i < strLine.length(); i++)
		{
			if (!Character.isWhitespace(strLine.charAt(i)))
				return false;
		}
		return true;	// Blank line
		
	}
	/**
	 * Wait for this many seconds.
	 */
	public void waitSec(int iSec)
	{
		try	{
			Thread.sleep(iSec * 1000);		// Wait
		} catch (InterruptedException ex)	{
			ex.printStackTrace();
		}
	}
	/**
	 * Wait for this many seconds.
	 */
	public void waitTime(int iHours, int iMinutes, int iSecs)
	{
		try	{
			int iTime = iHours * 60;		// Minutes
			iTime = (iTime + iMinutes) * 60;	// Seconds
			iTime = (iTime + iSecs) * 1000;		// Milliseconds
			Thread.sleep(iTime);		// Wait
		} catch (InterruptedException ex)	{
			ex.printStackTrace();
		}
	}
	/**
	 * Convert milliseconds to minutes
	 */
	public long msToMinutes(long lTimeConnectionLost)
	{
		lTimeConnectionLost = lTimeConnectionLost / 1000;	// Seconds
		long lMinConnectionLost = lTimeConnectionLost / 60;	// Minutes
		return lMinConnectionLost;
	}
}
