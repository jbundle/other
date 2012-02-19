/*
 * Servlet.java
 *
 * Created on April 8, 2000, 4:03 AM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package com.tourstudio.weblook;

/**
 * @(#)Servlet.java	0.00 12-Feb-97 Don Corley
 *
 * Copyright © 2012 jbundle.org. All Rights Reserved.
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
public class DirectoryXMLScanner extends Object
{

	/**
	  * Creates new Servlet.
	  */
	public DirectoryXMLScanner()
	{
		super();
	}
	/**
	 * Process an HTML get or post.
	 * @exception	ServletException From inherited class.
	 * @exception	IOException From inherited class.
	 */
	public void scanOutXML(PrintWriter out, String strDirectory)
		throws IOException
	{
		File dir = new File(strDirectory);
		if (!dir.isDirectory())
			return;
		File[] rgFiles = dir.listFiles(new Filter());
		out.write("<directory>");
		out.write("		<name>" + strDirectory + "</name>");
		for (int x = 0; x < rgFiles.length; x++)
		{
			for (int y = x + 1; y < rgFiles.length; y++)
			{
				if (rgFiles[x].getName().compareTo(rgFiles[y].getName()) > 0)
				{
					File file = rgFiles[x];
					rgFiles[x] = rgFiles[y];
					rgFiles[y] = file;
				}
			}
		}
		for (int i = 0; i < rgFiles.length; i++)
		{
			File file = rgFiles[i];
			out.write("	<file>");
			out.write("		<name>" + file.getName() + "</name>");
			out.write("		<size>" + file.length() + "</size>");
			out.write("	</file>");
		}
		out.write("</directory>");
	}
	/**
	 *
	 */
	class Filter extends Object
	 implements FilenameFilter
	{
		public Filter()
		{
			super();
		}
		public boolean accept(File dir, String name)
		{
			if (name != null)
				if (name.startsWith("localhost_access"))
					return true;
			return false;
		}
	}
}
