/*
 * Servlet.java
 *
 * Created on April 8, 2000, 4:03 AM
 */
 
package com.tourstudio.weblook;

/**
 * @(#)Servlet.java	0.00 12-Feb-97 Don Corley
 *
 * Copyright (c) 2002 jbundle.org. All Rights Reserved.
 *		don@tourgeek.com
 */

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/** 
 * WebLook Servlet.
 * This Servlet is used to upload a file to a server.
 * @author  Don Corley
 * @version 1.0.0
 */
public class LookServlet extends HttpServlet
{

	public static final String RETURN = "\n";
	public static final String BLANK = "";
	public static final String YES = "yes";
	public static final String NO = "no";
	
	public static final String FILE_PARAM = "file";
	public static final String MINUS_PARAM = "minus";
	public static final String PLUS_PARAM = "plus";
	public static final String EXCLUDE_PARAMS_PARAM = "noparams";
	public static final String ANALYZE_PARAMS_PARAM = "params";

	public static final String TITLE = "Upload files";

	/**
	  * Creates new Servlet.
	  */
	public LookServlet()
	{
		super();
	}
	/**
	 * Returns the servlet info.
	 */ 
	public String getServletInfo()
	{
		return "This the Servlet receives files over http";
	}
	/**
	 * Init method.
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
	 * Process an HTML get.
	 * @exception	ServletException From inherited class.
	 * @exception	IOException From inherited class.
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res) 
		throws ServletException, IOException
	{
		this.doProcess(req, res);
	}
	/**
	 * Process an HTML post.
	 * @exception	ServletException From inherited class.
	 * @exception	IOException From inherited class.
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse res) 
		throws ServletException, IOException
	{
		this.doProcess(req, res);
	}
	/**
	 * Process an HTML get or post.
	 * @exception	ServletException From inherited class.
	 * @exception	IOException From inherited class.
	 */
	public void doProcess(HttpServletRequest req, HttpServletResponse res) 
		throws ServletException, IOException
	{
		PrintStream out = System.out;
		Properties properties = new Properties();
		
		String strReceiveMessage = BLANK;
		this.sendForm(req, res, strReceiveMessage, properties);
	}
	/*
	 * Get the title for this servlet (Override to change).
	 * @return The screen title.
	 */
	public String getTitle()
	{
		return TITLE;
	}
	/**
	 * Process an HTML get or post.
	 * @exception	ServletException From inherited class.
	 * @exception	IOException From inherited class.
	 */
	public void sendForm(HttpServletRequest req, HttpServletResponse res, String strReceiveMessage, Properties properties)
		throws ServletException, IOException
	{
        res.setContentType("text/xml");
        PrintWriter out = res.getWriter();

		out.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");

		String strFile = req.getParameter(FILE_PARAM);
		if (strFile == null)
		{
			out.write("<?xml-stylesheet type=\"text/xsl\" href=\"docs/directory.xsl\"?>");

			DirectoryXMLScanner scanner = new DirectoryXMLScanner();

			scanner.scanOutXML(out, this.getDir());
		}
		else
		{
			String[] strPlus = req.getParameterValues(PLUS_PARAM);
			String[] strMinus = req.getParameterValues(MINUS_PARAM);
			String[] rgstrExcludeParams = req.getParameterValues(EXCLUDE_PARAMS_PARAM);
			String[] rgstrAnalyzeParams = req.getParameterValues(ANALYZE_PARAMS_PARAM);
			boolean bExcludeParams = false;
			if (rgstrExcludeParams != null) if (rgstrExcludeParams.length > 0) if (YES.equalsIgnoreCase(rgstrExcludeParams[0]))
				bExcludeParams = true;
			boolean bAnalyzeParams = false;
			if (rgstrAnalyzeParams != null) if (rgstrAnalyzeParams.length > 0) if (YES.equalsIgnoreCase(rgstrAnalyzeParams[0]))
				bAnalyzeParams = true;
			
			out.write("<?xml-stylesheet type=\"text/xsl\" href=\"docs/file.xsl\"?>");

			FileXMLScanner scanner = new FileXMLScanner();

			scanner.scanOutXML(out, this.getDir(), strFile, strPlus, strMinus, bExcludeParams, bAnalyzeParams);
		}
	}
	/*
	 * Get the temporary directory name.
	 */
	public String getDir()
	{
		String strTargetDirectory = System.getProperty("user.dir");
		String TOMCAT = "tomcat/";
		int iIndex = strTargetDirectory.indexOf(TOMCAT);
		if (iIndex != -1)
			strTargetDirectory = strTargetDirectory.substring(0, iIndex + TOMCAT.length()) + "logs";
		return strTargetDirectory;
	}

}
