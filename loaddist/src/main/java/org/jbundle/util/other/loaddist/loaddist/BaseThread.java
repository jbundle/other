/*
 * This is the main proxy program.  
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.util.other.loaddist.loaddist;

import java.net.*;  
import java.io.*;  
import java.util.*;

import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.spi.*;
import java.nio.charset.*;

/**  
 * A Base Thread is a thread that's ready to handle a job.  
 */  
public class BaseThread extends Thread  
{
    /**
     * My parent thread pool.
     */
    protected ThreadPool m_pool = null;
    /**
     * For information only (inbound or outbound).
     */
    protected boolean m_bInbound = false;

    /**  
     * Constructor.  
     */  
    public BaseThread()  
    {  
    	super();  
    }  
    /**  
     * Constructor.
     * @param pool My parent thread pool.
     */  
    public BaseThread(ThreadPool pool)
    {  
    	this();  
	    this.init(pool);
    }  
    /**  
     * Constructor.  
     * @param pool My parent thread pool.
     */  
    public void init(ThreadPool pool)
    {
        m_pool = pool;
    }
    /**
     * Set the thread type (for debugging).
     */
    public void setThreadType(boolean bInbound)
    {
        m_bInbound = bInbound;
    }
    /**  
     * Start this thread.
     * Wait to get notified. When I am notified, call handle request.
     */  
    public void run()  
    {  
        while (true)
    	{
            synchronized(this)
            {
                m_pool.freeThisThread(this);  // Place onto the free thread pool.
                try
                {  
                    if (Debug.isOutput())
                        Debug.pl("socketThread count " + m_pool.m_freeThreads.size() + " waiting " + m_pool.m_activeThreads.size() + " active " + this);
                    this.wait();    // Wait to get woken up.  
                    if (Debug.isOutput())
                        Debug.pl("socketThread notified " + this);
                } catch (InterruptedException ex)	 {  
                    ex.printStackTrace();  
                }  
            }  
            this.handleRequest();    // Now process this socket request.
    	}  
    }
    /**
     * Handle the request.
     * Override this method to handle the request.
     */
    public void handleRequest()
    {
    }
}
