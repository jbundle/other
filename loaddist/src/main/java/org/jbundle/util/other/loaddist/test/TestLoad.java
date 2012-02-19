/*
 * TestLoad.java
 *
 * Created on May 19, 2003, 2:54 AM

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.util.other.loaddist.test;

//import com.oreilly.servlet.HttpMessage;

import java.net.*;
import java.io.*;
import java.lang.*;

/**
 *
 * @author  don
 */
public class TestLoad extends Thread
{
    public static int READ_DELAY = 400;
    public static int SPIN_DELAY = 1300;
    public static int CREATE_DELAY = 3500;
    
    /**
     * Creates a new instance of TestLoad
     */
    public TestLoad()
    {
        super();
    }
    /**
     * Creates a new instance of TestLoad
     */
    public TestLoad(String[] args)
    {
        this();
        this.init(args);
    }
    /**
     * Creates a new instance of TestLoad
     */
    public void init(String[] args)
    {
        if (args != null)
            if (args.length > 0)
                strURL = args[0];
    }
    protected String strURL = "http://www.donandann.com";
    /**
     * Creates a new instance of TestLoad
     */
    public void run()
    {
//        URL url = new URL(getCodeBase(), "/servlet/ServletName");
        try {
            while (true)
            {
                URL url = new URL(strURL);

                url.openConnection();
                InputStream in = url.openStream();

                byte[] b = new byte[1000];
                int iTotal = 0;
                int iLen;
                while ((iLen = in.read(b)) != -1)
                {
                    iTotal += iLen;
                    this.sleep(READ_DELAY);
                }
                System.out.println("read " + iTotal + " bytes " + this);
                this.sleep(SPIN_DELAY);
                in.close();
            }
            
        } catch (InterruptedException ex)    {
            ex.printStackTrace();
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
        
    }
    /**
     * Creates a new instance of TestLoad
     */
    public static void main(String[] args)
    {
        try {
            while (true)
            {
                TestLoad test = new TestLoad(args);
                test.start();
                Thread.currentThread().sleep(CREATE_DELAY);
            }
        } catch (InterruptedException ex)    {
            ex.printStackTrace();
        }
    }
}
