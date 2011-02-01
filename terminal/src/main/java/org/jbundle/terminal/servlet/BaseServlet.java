package org.jbundle.terminal.servlet;

/**
 * @(#)DBServlet.java	0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 1997 jbundle.org. All Rights Reserved.
 *		Don_Corley@msn.com
 */

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.jbundle.terminal.server.*;

import java.rmi.*;
import java.awt.event.*;
import java.awt.*;


/**
 * BaseServlet
 * 
 * This is the base servlet.
 * <pre>
 * You have must override this class with one of two options:
 * 1 - A Servlet that asks the view to covert itself to HTML (remote processing).
 * or
 * 2 - A Servlet that asks for the current model and then converts it to HTML (servlet processing).
 * </pre>
 * <p>
 * The possible params are:
 * <pre>
 *	record - Create a default HTML screen for this record (Display unless "move" param)
 *	screen - Create this HTML screen
 *	limit - For Displays, limit the records displayed
 *	form - If "yes" display the imput form above the record display
 *	move - HTML Input screen - First/Prev/Next/Last/New/Refresh/Delete
 *	applet - applet, screen=applet screen
 *				applet params: archive/id/width/height/cabbase
 *	menu - Display this menu page
 * </pre>
 */
public abstract class BaseServlet extends HttpServlet
	implements org.jbundle.terminal.TerminalConstants
{
	protected RmiOut m_rmiOut = null;

	public static final String PARAM_DATA = "data";
	public static final String PARAM_DELAY = "delay";
	public static final String PARAM_REFRESH = "refresh";

	public static final String PARAM_ENDOFLINE = "endofline";
	public static final String PARAM_RETURNIFDATA = "returnifdata";
	public static final String PARAM_NEWLINEIFDATA = "newlineifdata";
	public static final String PARAM_RETURN = "return";
	public static final String PARAM_NEWLINE = "newline";
	public static final String PARAM_NONE = "none";
	
	public static final String PARAM_SHOW = "showKeyboard";
	public static final String PARAM_DONT_SHOW_KEYBOARD = "Don't Show Keyboard";
	public static final String PARAM_SHOW_KEYBOARD = "Show Keyboard";
	
	public static final String PARAM_FUNCTION = "function";
	public static final String PARAM_MODIFIER = "modifier";
	public static final String VALUE_SHIFT = "shift";
	public static final String VALUE_CTRL = "ctrl";
	public static final String VALUE_ALT = "alt";
	
	public static final String gstrBlank = "";
	public static final String SELECTED = "SELECTED";
	public static final String TITLE = "Terminal";
	public static final String PARAM_HOST = "host";

	/**
	 * returns the servlet info
	 */ 
	public String getServletInfo()
	{
		return "This the main servlet";
	}
	/**
	 * init method.
	 * @exception	ServletException From inherited class.
	 */
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
	}
	/**
	 * Destroy this applet.
	 */
	public void destroy()
	{
		super.destroy();
	}
	/**
	 *	process an HTML get.
	 * @exception	ServletException From inherited class.
	 * @exception	IOException From inherited class.
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res) 
		throws ServletException, IOException
	{
		this.doProcess(req, res);
	}
	/**
	 *	process an HTML post.
	 * @exception	ServletException From inherited class.
	 * @exception	IOException From inherited class.
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res) 
		throws ServletException, IOException
	{
		this.doProcess(req, res);
	}
	/**
	 *	process an HTML get or post.
	 * @exception	ServletException From inherited class.
	 * @exception	IOException From inherited class.
	 */
	public void doProcess(HttpServletRequest req, HttpServletResponse res) 
		throws ServletException, IOException
	{
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
		String[] strParamData = req.getParameterValues(PARAM_DATA);		// Display record
		String strData = gstrBlank;
		float fDelay = 1.0f;
		String strDelay = "1.0";
		char chReturn = '\r';
		boolean bShowKeyboard = false;
		int iFunction = 0;
		int iModifiers = 0;

		if (strParamData != null)
			if (strParamData.length > 0)
		{
			strData = strParamData[0];
			if (strData == null)
				strData = gstrBlank;
		}
		String[] strSelect = {
			gstrBlank,
			gstrBlank,
			gstrBlank,
			gstrBlank,
			gstrBlank
		};
		String strNoReturn = PARAM_RETURNIFDATA;
		String[] strParamNoReturn = req.getParameterValues(PARAM_ENDOFLINE);
		if ((strParamNoReturn != null)
			&& (strParamNoReturn.length > 0)
				&& (strParamNoReturn[0] != null)
					&& (strParamNoReturn[0].length() > 0))
		{
			strNoReturn = strParamNoReturn[0];
			if (strNoReturn.equalsIgnoreCase(PARAM_RETURNIFDATA))
			{
				strSelect[0] = SELECTED;
				if (strData.length() == 0)
					chReturn = 0;
				else
					chReturn = '\r';
			}
			else if (strNoReturn.equalsIgnoreCase(PARAM_NEWLINEIFDATA))
			{
				strSelect[1] = SELECTED;
				if (strData.length() == 0)
					chReturn = 0;
				else
					chReturn = '\n';
			}
			else if (strParamNoReturn[0].equalsIgnoreCase(PARAM_RETURN))
			{
				strSelect[2] = SELECTED;
				chReturn = '\r';
			}
			else if (strParamNoReturn[0].equalsIgnoreCase(PARAM_NEWLINE))
			{
				strSelect[3] = SELECTED;
				chReturn = '\n';
			}
			else if (strNoReturn.equalsIgnoreCase(PARAM_NONE))
			{
				strSelect[4] = SELECTED;
				chReturn = 0;
			}
		}
		else
		{
			strSelect[0] = SELECTED;
			if (strData.length() == 0)
				chReturn = 0;
			else
				chReturn = '\r';
		}

		if ((strData != null) && (chReturn != 0))
			strData += chReturn;
		
		String[] strParamDelay = req.getParameterValues(PARAM_DELAY);
		if (strParamDelay != null)
			if (strParamDelay.length > 0)
		{
			strDelay = strParamDelay[0];
			if ((strDelay == null)
				|| (strDelay.length() == 0))
					fDelay = 1.0f;
			else
			{
				try	{
					fDelay = Float.parseFloat(strDelay);
				} catch(NumberFormatException ex)	{
					fDelay = 1.0f;
				}
			}
		}
		if (fDelay > 5)
			fDelay = 5;		// Max 5 second delay
		strDelay = Float.toString(fDelay);

		float fRefresh = 0.0f;
		String strRefresh;
		String[] strParamRefresh = req.getParameterValues(PARAM_REFRESH);
		if (strParamRefresh != null)
			if (strParamRefresh.length > 0)
		{
			strRefresh = strParamRefresh[0];
			if ((strRefresh == null)
				|| (strRefresh.length() == 0))
					fRefresh = 0.0f;
			else
			{
				try	{
					fRefresh = Float.parseFloat(strRefresh);
				} catch(NumberFormatException ex)	{
					fRefresh = 0.0f;
				}
			}
		}
		if (fRefresh != 0)
			if (fRefresh < 5) 
				fRefresh = 5;		// Min 5 second delay
		strRefresh = Float.toString(fRefresh);
		
		String[] strParamShow = req.getParameterValues(PARAM_SHOW);
		if ((strParamShow != null)
			&& (strParamShow.length > 0)
				&& (strParamShow[0] != null)
					&& (strParamShow[0].length() > 0))
		{
			if (strParamShow[0].equalsIgnoreCase(PARAM_SHOW_KEYBOARD))
				bShowKeyboard = true;
		}

		String[] strParamFunction = req.getParameterValues(PARAM_FUNCTION);
		if ((strParamFunction != null)
			&& (strParamFunction.length > 0)
				&& (strParamFunction[0] != null)
					&& (strParamFunction[0].length() > 0))
		{
			bShowKeyboard = true;
			for (int i = 0; i < m_strFunctions.length; i++)
			{
				if (strParamFunction[0].equalsIgnoreCase(m_strFunctions[i]))
					iFunction = m_iFunctions[i];
			}
			if (iFunction != 0)
			{
				strData = gstrBlank;
				String[] strParamModifiers = req.getParameterValues(PARAM_MODIFIER);
				if (strParamModifiers != null)
				{
					for (int i = 0; i < strParamModifiers.length; i++)
					{
						if (strParamModifiers[i].equalsIgnoreCase(VALUE_SHIFT))
							iModifiers |= Event.SHIFT_MASK;
						if (strParamModifiers[i].equalsIgnoreCase(VALUE_CTRL))
							iModifiers |= Event.CTRL_MASK;
						if (strParamModifiers[i].equalsIgnoreCase(VALUE_ALT))
							iModifiers |= Event.ALT_MASK;
					}
				}
			}
		}
		String strHost = gstrBlank;
		String[] strParamHost = req.getParameterValues(PARAM_HOST);
		if (strParamHost != null)
			if (strParamHost.length > 0)
				if (strParamHost[0] != null)
					strHost = strParamHost[0];

		String strServletPath = null;
		try	{
			strServletPath = req.getRequestURI();	//getServletPath();
		} catch (Exception ex)
		{
			strServletPath = null;
		}
		if (strServletPath == null)
			strServletPath = "./com.tourstudio.terminal.Servlet";

		String strRefreshTag = "";
		if (fRefresh >= 5.0)
			if (strRefresh.length() > 0)
				strRefreshTag = "<META HTTP-EQUIV=REFRESH CONTENT=\"" + strRefresh + "; URL=" + strServletPath + "?refresh=" + strRefresh + "\">" + RETURN;
		out.write("<html>" + RETURN +
						"<head>" + RETURN +
						"<title>" + TITLE + "</title>" + RETURN +
		strRefreshTag +
		"<script language=\"JavaScript\">" + RETURN +
		"<!-- Hide from old browsers" + RETURN +
		//x"var bEnableAutoSubmit = true;" + RETURN +
		"function focusFirstField()" + RETURN +
		"{" + RETURN +
		"document.forms[0]." + PARAM_DATA + ".focus();" + RETURN +
		"}" + RETURN +
		"function submitFirstForm()" + RETURN +
		"{" + RETURN +
		//x"if (bEnableAutoSubmit == true)" + RETURN +
		"\tdocument.forms[0].submit();" + RETURN +
		"}" + RETURN +
		"// End hide -->" + RETURN +
		"</script>" + RETURN +
						"</head>" + RETURN +
						"<body onLoad=\"focusFirstField();\">" + RETURN +
						"<center><b>" + TITLE + "</b></center><p>" + RETURN +
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

		m_rmiOut = this.getRemoteView(req, strHost);
		if (((strData != null) && (strData.length() > 0))
			|| (iFunction != 0))
		{
			try	{
				m_rmiOut.sendThisString(strData, iFunction, iModifiers);
			} catch (RemoteException ex)	{
				// Maybe the thread is broken, try again.
				m_rmiOut = null;	// Clear cache
				m_rmiOut = this.getRemoteView(req, strHost);
				m_rmiOut.sendThisString(strData, iFunction, iModifiers);	// Try again				
			}
			try	{
				if (fDelay > 0)
					Thread.sleep((long)(fDelay * 1000));
			} catch (InterruptedException ex)	{
				// Ignore
			}
		}
		String strScreen = "";
		try	{
			strScreen = this.getCurrentView();
		} catch (RemoteException ex)	{
			// Maybe the thread is broken, try again.
			m_rmiOut = null;	// Clear cache
			m_rmiOut = this.getRemoteView(req, strHost);
			strScreen = this.getCurrentView();	// Try again				
		}
		out.write(strScreen);

		out.write("</pre>" + RETURN);

		out.write("<p><form action=" + strServletPath + " method=get>" + RETURN +
		//x	"Data: <input type=text name=" + PARAM_DATA + " size=50 maxlength=200 value=\"\" onChange=\"submitFirstForm();\" onBlur=\"bEnableAutoSubmit = false;\" onFocus=\"bEnableAutoSubmit = true;\">" + RETURN +
		"Data: <input type=text name=" + PARAM_DATA + " size=50 maxlength=200 value=\"\" onSelect=\"submitFirstForm();\">" + RETURN +
		"<input type=submit name=submitButton value=Submit>" + RETURN +

		"<select name=\"" + PARAM_ENDOFLINE + "\">" + RETURN +
		"<option " + strSelect[0] + " value=" + PARAM_RETURNIFDATA + ">Return if data</option>" + RETURN +
		"<option " + strSelect[1] + " value=" + PARAM_NEWLINEIFDATA + ">New Line if data</option>" + RETURN +
		"<option " + strSelect[2] + " value=" + PARAM_RETURN + ">Return</option>" + RETURN +
		"<option " + strSelect[3] + " value=" + PARAM_NEWLINE + ">New Line</option>" + RETURN +
		"<option " + strSelect[4] + " value=" + PARAM_NONE + ">None</option>" + RETURN +
		"</select>" + RETURN +

		" Delay: <input type=text name=" + PARAM_DELAY + " size=7 value=\"" + strDelay + "\">" + RETURN);
		if ((strHost != null) && (strHost.length() > 0))
			out.write("<input type=hidden name=\"" + PARAM_HOST + "\" value=\"" + strHost + "\">" + RETURN);
		if (bShowKeyboard)
			this.writeShowButton(out, "hidden", !bShowKeyboard);
		out.write("</form></p>" + RETURN);

		out.write("<p><form action=" + strServletPath + " method=get>" + RETURN);
		out.write("<input type=hidden name=\"" + PARAM_ENDOFLINE + "\" value=\"" + strNoReturn + "\">" + RETURN);
		out.write("<input type=hidden name=\"" + PARAM_DELAY + "\" value=\"" + strDelay + "\">" + RETURN);
		if ((strHost != null) && (strHost.length() > 0))
			out.write("<input type=hidden name=\"" + PARAM_HOST + "\" value=\"" + strHost + "\">" + RETURN);
		if (bShowKeyboard)
			this.writeKeyboard(out, req);
		this.writeShowButton(out, "submit", bShowKeyboard);
		out.write("</form></p>" + RETURN);
//---------
		out.write("<p><form action=" + strServletPath + " method=get>" + RETURN);
		out.write("<input type=hidden name=\"" + PARAM_ENDOFLINE + "\" value=\"" + strNoReturn + "\">" + RETURN);
		out.write("<input type=hidden name=\"" + PARAM_DELAY + "\" value=\"" + strDelay + "\">" + RETURN);
		out.write("Refresh: <input type=text name=" + PARAM_REFRESH + " size=5 maxlength=5 value=\"" + strRefresh + "\">" + RETURN);
		if ((strHost != null) && (strHost.length() > 0))
			out.write("<input type=hidden name=\"" + PARAM_HOST + "\" value=\"" + strHost + "\">" + RETURN);
		out.write("<input type=submit name=submitButton value=Submit>" + RETURN);
		out.write("</form></p>" + RETURN);
//---------
		out.write("</body>" + RETURN +
						"</html>" + RETURN);
	}
	/**
	 * Write the button to show or hide the keyboard.
	 */
	public void writeShowButton(PrintWriter out, String strType, boolean bShowKeyboard)
	{
		if (strType == null)
			strType = "submit";
		out.write("<input type=" + strType + " name=" + PARAM_SHOW + " value=\"");
		if (bShowKeyboard)
			out.write(PARAM_DONT_SHOW_KEYBOARD);
		else
			out.write(PARAM_SHOW_KEYBOARD);
		out.write("\">" + RETURN);
	}
	public static final int[] m_iFunctions =
	{
		KeyEvent.VK_F1,
		KeyEvent.VK_F2,
		KeyEvent.VK_F3,
		KeyEvent.VK_F4,
		KeyEvent.VK_F5,
		KeyEvent.VK_F6,
		KeyEvent.VK_F7,
		KeyEvent.VK_F8,
		KeyEvent.VK_F9,
		KeyEvent.VK_F10,
		KeyEvent.VK_F11,
		KeyEvent.VK_F12,
		KeyEvent.VK_HOME,
		KeyEvent.VK_END,
		KeyEvent.VK_PAGE_UP,
		KeyEvent.VK_PAGE_DOWN,
		KeyEvent.VK_UP,
		KeyEvent.VK_DOWN,
		KeyEvent.VK_LEFT,
		KeyEvent.VK_RIGHT,
		KeyEvent.VK_INSERT,
		KeyEvent.VK_ENTER,
		KeyEvent.VK_BACK_SPACE,
		KeyEvent.VK_TAB,
		KeyEvent.VK_ESCAPE,
		KeyEvent.VK_DELETE,
		KeyEvent.VK_A,
		KeyEvent.VK_B,
		KeyEvent.VK_C,
		KeyEvent.VK_D,
		KeyEvent.VK_E,
		KeyEvent.VK_F,
		KeyEvent.VK_G,
		KeyEvent.VK_H,
		KeyEvent.VK_I,
		KeyEvent.VK_J,
		KeyEvent.VK_K,
		KeyEvent.VK_L,
		KeyEvent.VK_M,
		KeyEvent.VK_N,
		KeyEvent.VK_O,
		KeyEvent.VK_P,
		KeyEvent.VK_Q,
		KeyEvent.VK_R,
		KeyEvent.VK_S,
		KeyEvent.VK_T,
		KeyEvent.VK_U,
		KeyEvent.VK_V,
		KeyEvent.VK_W,
		KeyEvent.VK_X,
		KeyEvent.VK_Y,
		KeyEvent.VK_Z,
		KeyEvent.VK_0,
		KeyEvent.VK_1,
		KeyEvent.VK_2,
		KeyEvent.VK_3,
		KeyEvent.VK_4,
		KeyEvent.VK_5,
		KeyEvent.VK_6,
		KeyEvent.VK_7,
		KeyEvent.VK_8,
		KeyEvent.VK_9
	};
	public static final String[] m_strFunctions =
	{
		"F1",
		"F2",
		"F3",
		"F4",
		"F5",
		"F6",
		"F7",
		"F8",
		"F9",
		"F10",
		"F11",
		"F12",
		"Home",
		"End",
		"Page Up",
		"Page Down",
		"Up",
		"Down",
		"Left",
		"Right",
		"Insert",
		"Enter",
		"Backspace",
		"Tab",
		"Escape",
		"Delete",
		"A",
		"B",
		"C",
		"D",
		"E",
		"F",
		"G",
		"H",
		"I",
		"J",
		"K",
		"L",
		"M",
		"N",
		"O",
		"P",
		"Q",
		"R",
		"S",
		"T",
		"U",
		"V",
		"W",
		"X",
		"Y",
		"Z",
		"0",
		"1",
		"2",
		"3",
		"4",
		"5",
		"6",
		"7",
		"8",
		"9"
	};
	/**
	 * Write the HTML keyboard.
	 */
	public void writeKeyboard(PrintWriter out, HttpServletRequest req)
	{
		int i = 0;
		for (; i < m_strFunctions.length; i++)
		{
			out.write("<input type=submit name=" + PARAM_FUNCTION + " value=\"" + m_strFunctions[i] + "\">");
			if (m_strFunctions[i].equalsIgnoreCase("F12"))
				break;
		}
		out.write(" <input type=checkbox name=" + PARAM_MODIFIER + " value=" + VALUE_SHIFT + this.selectIfParam(req, VALUE_SHIFT) + "> Shift ");
		out.write(" <input type=checkbox name=" + PARAM_MODIFIER + " value=" + VALUE_CTRL + this.selectIfParam(req, VALUE_CTRL) + "> Control ");
		out.write(" <input type=checkbox name=" + PARAM_MODIFIER + " value=" + VALUE_ALT + this.selectIfParam(req, VALUE_ALT) + "> Alt ");
		out.write("</p>");

		out.write("<p>");
		for (i++; i < m_strFunctions.length; i++)
		{
			if (m_strFunctions[i].equalsIgnoreCase("A"))
				break;
			out.write("<input type=submit name=" + PARAM_FUNCTION + " value=\"" + m_strFunctions[i] + "\">");
		}
		out.write("</p>");

		out.write("<p>");
		for ( ; i < m_strFunctions.length; i++)
		{
			out.write("<input type=submit name=" + PARAM_FUNCTION + " value=\"" + m_strFunctions[i] + "\">");
			if (m_strFunctions[i].equalsIgnoreCase("Z"))
				break;
		}
		for (i++ ; i < m_strFunctions.length; i++)
		{
			out.write("<input type=submit name=" + PARAM_FUNCTION + " value=\"" + m_strFunctions[i] + "\">");
		}
		out.write("</p>");
	}
	/**
	 * Return "selected" if the "modified" param is set to this value.
	 */
	public String selectIfParam(HttpServletRequest req, String strValue)
	{
		String strSelected = "";
		String[] strParamShow = req.getParameterValues(PARAM_MODIFIER);
		if (strParamShow != null)
		{
			for (int i = 0; i < strParamShow.length; i++)
			{
				if (strParamShow[i] != null)
					if (strParamShow[i].equalsIgnoreCase(strValue))
						return " checked";
			}
		}
		return strSelected;
	}
	/**
	 * Get the connection to the remote view.
	 */
	public RmiOut getRemoteView(HttpServletRequest req, String strHost)
	{
		if (m_rmiOut == null)
			this.setProperties(req, strHost);
		return m_rmiOut;
	}
	/**
	 * Set this control up to implement these properties.
	 */
	public void setProperties(HttpServletRequest req, String strHost)
	{
		try {
			if ((strHost == null) ||
				(strHost.length() == 0))
					strHost = this.getInitParameter(PARAM_HOST);
			if ((strHost == null) ||
				(strHost.length() == 0))
			{
				strHost = req.getServerName();
				if ((strHost == null) ||
					(strHost.length() == 0))
						strHost = "localhost";
			}
			m_rmiOut = (RmiOut)Naming.lookup("//" + strHost + '/' + this.getServerName());
		} catch (Exception e) {
			System.out.println("Servlet exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	/**
	 * Get the name of the RMI Server.
	 */
	public abstract String getServerName();
	/**
	 * Get the HTML code from the RMI Server.
	 */
	public abstract String getCurrentView() throws RemoteException;
}
