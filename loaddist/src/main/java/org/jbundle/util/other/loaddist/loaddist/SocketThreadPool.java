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
 * A Socket Thread is a thread that's job is to handle this socket.  
 */  
public class SocketThreadPool extends ThreadPool 
{

    /**  
     * Constructor.  
     */  
    public SocketThreadPool()  
    {  
    	super();  
    }  
    /**  
     * Constructor.  
     */  
    public SocketThreadPool(int iMinThreadCount, int iMaxThreadCount, int iThreadPoolIncrement)
    {  
    	this();
	    this.init(iMinThreadCount, iMaxThreadCount, iThreadPoolIncrement);
    }  
    /**  
     * Constructor.  
     */  
    public void init(int iMinThreadCount, int iMaxThreadCount, int iThreadPoolIncrement)
    {
        super.init(iMinThreadCount, iMaxThreadCount, iThreadPoolIncrement);
    }
    /**
     * Create a new Socket thread and push it onto the free thread stack. 
     */  
    public SocketThread createNewThread(String strThreadType)
    {
        return new PipeThread(this);
    }

}
