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
public class FileXMLScanner extends Object
{
	public static int IP = 1;
	public static int URL = 7;
	public static int PROTOCOL = 8;
	public static int BYTES = 9;
	
	public static String BLANK = "";

	/**
	  * Creates new Servlet.
	  */
	public FileXMLScanner()
	{
		super();
	}
	/**
	 * Process an HTML get or post.
	 * @exception	ServletException From inherited class.
	 * @exception	IOException From inherited class.
	 */
	public void scanOutXML(PrintWriter out, String strDirectory, String strFilename, String[] strPlus, String[] strMinus, boolean bExcludeParams, boolean bAnalyzeParams)
		throws IOException
	{
		File dir = new File(strDirectory + '/' + strFilename);
		if (dir.isDirectory())
			return;

		try	{
			FileReader is = new FileReader(strDirectory + '/' + strFilename);
			BufferedReader r = new BufferedReader(is);
			String string = null;
			Hashtable ht = new Hashtable();
			Set setstrExtensions = new HashSet();
			int iCount = 0;
			int iBytes = 0;
			while ((string = r.readLine()) != null)
			{
				StringTokenizer st = new StringTokenizer(string, " \"", false);
				Data data = new Data();
				int iTokenCount = 0;
				while (st.hasMoreTokens())
				{
					iTokenCount++;
					string = st.nextToken();
					if (iTokenCount == IP)
						data.m_IP = string;
					if (iTokenCount == URL)
					{
						if (bExcludeParams)
							if (string.indexOf('?') != -1)
								string = string.substring(0, string.indexOf('?'));
						if (bAnalyzeParams)
							if (string.indexOf('?') != -1)
								string = string.substring(string.indexOf('?') + 1);
						data.m_URL = string;
					}
					if (iTokenCount == PROTOCOL)
						if (!string.startsWith("HTTP"))
						{
							data.m_URL += " " + string;
							iTokenCount--;
						}
					if (iTokenCount == BYTES)
						data.m_iBytes = Integer.parseInt(string);
				}
				if (!this.filterURL(data.m_URL, strPlus, strMinus, setstrExtensions))
					continue;
				iCount++;
				iBytes += data.m_iBytes;
				if (ht.get(data.m_URL) == null)
					ht.put(data.m_URL, data);
				else
				{
					int iThisBytes = data.m_iBytes;
					data = (Data)ht.get(data.m_URL);
					data.m_iCount++;
					data.m_iBytes += iThisBytes;
				}
 			}
			Comparator comparator = new Test();
			TreeMap tm = new TreeMap(comparator);
			Iterator iterator = ht.values().iterator();
			while (iterator.hasNext())
			{
				Data data = (Data)iterator.next();
				tm.put(new Integer(data.m_iCount), data);
			}
			out.println("<file>");
			this.printXML(out, "directory", strDirectory);
			this.printXML(out, "name", strFilename);
			iterator = tm.values().iterator();
			while (iterator.hasNext())
			{
				out.println("<data>");
				Data data = (Data)iterator.next();
				this.printXML(out, "url", data.m_URL);
				this.printXML(out, "count", Integer.toString(data.m_iCount));
				out.println("</data>");
			}
			this.printXML(out, "hits", Integer.toString(iCount));
			this.printXML(out, "bytes", Integer.toString(iBytes));
			this.printXML(out, "unique", Integer.toString(tm.size()));

			iterator = setstrExtensions.iterator();
			out.println("<extensions>");
			while (iterator.hasNext())
			{
				this.printXML(out, "extension", (String)iterator.next());
			}
			out.println("</extensions>");

			out.println("</file>");
		} catch (FileNotFoundException ex)	{
			ex.printStackTrace();
		} catch (IOException ex)	{
			ex.printStackTrace();
		}
		
	}
	/**
	 * Process an HTML get or post.
	 */
	public boolean filterURL(String strURL, String[] strPlus, String[] strMinus, Set setstrExtensions)
	{
		boolean bOkay = true;
		String strExtension = BLANK;
		if (strURL != null)
		{
			int iEndDot = strURL.indexOf('?');
			if (iEndDot == -1)
				iEndDot = strURL.length();
			int iStartDot = strURL.lastIndexOf('.', iEndDot - 1);
			if (iStartDot > 0)
				strExtension = strURL.substring(iStartDot + 1, iEndDot);
		}
		setstrExtensions.add(strExtension);
		if (strPlus != null)
			if (strPlus.length > 0)
		{
			bOkay = false;
			for (int i = 0; i < strPlus.length; i++)
			{
				if (strPlus[i].equalsIgnoreCase(strExtension))
					bOkay = true;
			}
		}
		if (strMinus != null)
			if (strMinus.length > 0)
		{
			for (int i = 0; i < strMinus.length; i++)
			{
				if (strMinus[i].equalsIgnoreCase(strExtension))
					bOkay = false;
			}
		}
		return bOkay;
	}
	/**
	 *
	 */
	public void printXML(PrintWriter out, String tag, String data)
	{
		out.println("<" + URLEncoder.encode(tag) + ">" + this.encode(data) + "</" + URLEncoder.encode(tag) + ">");
	}
	public String encode(String string)
	{
		string = string.replaceAll("<", "&lt;");
		string = string.replaceAll(">", "&gt;");
		string = string.replaceAll("\"", "&quot;");
//		string = string.replaceAll("'", "&quot;");
		string = string.replaceAll("&", "&amp;");
		return string;
	}
	/**
	 *
	 */
	class Data
	{
		public Data()
		{
			m_iCount = 1;
		}
		public String m_IP;
		public String m_URL;
		public int m_iBytes;
		public int m_iCount;
	}
	class Test implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			if (((Integer)o1).intValue() < ((Integer)o2).intValue())
				return 1;	// Reverse order
			else
				return -1;
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	}
}
