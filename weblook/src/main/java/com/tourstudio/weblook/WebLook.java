/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package com.tourstudio.weblook;

import java.lang.*;
import java.util.*;
import java.io.*;

/**
 *
 */
public class WebLook extends Object
{
	/**
	 *
	 */
	public WebLook()
	{
		super();
	}
	/**
	 *
	 */
	public static void main(String[] args)
	{
		WebLook app = new WebLook();
		app.run();
	}
	public static int IP = 1;
	public static int URL = 7;
	public static int PROTOCOL = 8;
	public static int BYTES = 9;
	/**
	 *
	 */
	public void run()
	{
		System.out.println("Hello");
		String filename = "/usr/local/java/web/tomcat/logs/localhost_access_log.2002-08-02.txt";
		try	{
			FileReader is = new FileReader(filename);
			BufferedReader r = new BufferedReader(is);
			String string = null;
			Hashtable ht = new Hashtable();
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
					System.out.println("Data: " + iTokenCount + " " + string);
					if (iTokenCount == IP)
						data.m_IP = string;
					if (iTokenCount == URL)
						data.m_URL = string;
					if (iTokenCount == PROTOCOL)
						if (!string.startsWith("HTTP"))
						{
							data.m_URL += " " + string;
							iTokenCount--;
						}
					if (iTokenCount == BYTES)
						data.m_iBytes = Integer.parseInt(string);
				}
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
				System.out.println("ht: " + ht);
 			}
			Comparator comparator = new Test();
			TreeMap tm = new TreeMap(comparator);
			Iterator iterator = ht.values().iterator();
			while (iterator.hasNext())
			{
				Data data = (Data)iterator.next();
				tm.put(new Integer(data.m_iCount), data);
			}
			iterator = tm.values().iterator();
			while (iterator.hasNext())
			{
				Data data = (Data)iterator.next();
				if (data.m_URL.endsWith(".gif"))
				System.out.println("Count: " + data.m_iCount + " url: " + data.m_URL);
			}
			iterator = tm.values().iterator();
			while (iterator.hasNext())
			{
				Data data = (Data)iterator.next();
				if (!data.m_URL.endsWith(".gif"))
				System.out.println("Count: " + data.m_iCount + " url: " + data.m_URL);
			}
			System.out.println("Hits: " + iCount);
			System.out.println("Bytes: " + iBytes);
			System.out.println("Unique: " + tm.size());

		} catch (FileNotFoundException ex)	{
			ex.printStackTrace();
		} catch (IOException ex)	{
			ex.printStackTrace();
		}
		
		System.out.println("Done");
	}
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
