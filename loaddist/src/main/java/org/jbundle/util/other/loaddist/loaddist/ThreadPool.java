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
public class ThreadPool extends Thread  
{
    /**
     * Minimum/starting threads.
     */
    public static final int MIN_THREAD_COUNT = 20;
    /**
     * Max thread count.
     */
    public static final int MAX_THREAD_COUNT = 10000;
    /**
     * Number of threads to add when the pool gets low (Make it even).
     */
    public static final int THREAD_POOL_INCREMENT = 8;
    /*
     * The one and only thread pool.
     */
    protected static ThreadPool m_threadPool = null;
    /**
     * Minimum/starting threads.
     */
    protected int m_iMinThreadCount = MIN_THREAD_COUNT;
    /**
     * Max thread count.
     */
    protected int m_iMaxThreadCount = MAX_THREAD_COUNT;
    /**
     * Number of threads to add when the pool gets low.
     */
    protected int m_iThreadPoolIncrement = THREAD_POOL_INCREMENT;
    /**
     * Active thread pool. 
     */
    protected Stack m_activeThreads = new Stack();
    /**
     * Free thread pool.
     */
    protected Stack m_freeThreads = new Stack();
    /**
     * Waiting to be notified.
     */
    protected boolean m_bWaiting = false;

    /**
     * Constructor.
     */
    public ThreadPool()
    {
    	super();
    }
    /**
     * Constructor.  
     */  
    public ThreadPool(int iMinThreadCount, int iMaxThreadCount, int iThreadPoolIncrement)
    {  
    	this();
	    this.init(iMinThreadCount, iMaxThreadCount, iThreadPoolIncrement);
    }  
    /**  
     * Constructor.  
     * @param iMinThreadCount Minimum/starting Threads.
     * @param iMaxThreadCount Maximum threads.
     * @param iThreadPoolIncrement Amount to bump threads when you need more.
     */  
    public void init(int iMinThreadCount, int iMaxThreadCount, int iThreadPoolIncrement)
    {
        m_bWaiting = false;
        m_threadPool = this;
        
        if (iMinThreadCount != -1)
            m_iMinThreadCount = iMinThreadCount;
        else
            m_iMinThreadCount = MIN_THREAD_COUNT;
        if (iMaxThreadCount != -1)
            m_iMaxThreadCount = iMaxThreadCount;
        else
            m_iMaxThreadCount = MAX_THREAD_COUNT;
        if (iThreadPoolIncrement != -1)
            m_iThreadPoolIncrement = iThreadPoolIncrement;
        else
            m_iThreadPoolIncrement = THREAD_POOL_INCREMENT;
        
        this.initThreadPool();
    }
    /**
     * Get the one and only thread pool.
     * NOTE: It would be extreemly unusual to have more than one pool, but it is possible,
     * in which case this static call should not be used.
     * @return The thread pool.
     */
    public static ThreadPool getThreadPool()
    {
        return m_threadPool;
    }
    /**
     * Start up the initial threads. 
     */
    public synchronized void initThreadPool()
    {  
        for (int i = 0; i < m_iMinThreadCount; i++)  
        {  
	        BaseThread socketThread = this.setupNewThread(null);  
            m_activeThreads.add(socketThread);  // Technically it is active until it hits wait (which it can't call until it is released from the removeActive call when I wait)
        }  
    }
    /**  
     * Create a new Socket thread and push it onto the free thread stack.
     * Don't override this method, override createNewSocketThread.
     * @return The new socket thread.
     */  
    public final SocketThread setupNewThread(String strThreadType)	
    {  
        SocketThread socketThread = this.createNewThread(strThreadType);
        if (socketThread != null)
        {
            socketThread.start();  
            if (Debug.isOutput())
                Debug.pl("SocketThread started");
        }
        return socketThread;  
    }
    /**  
     * Create a new Socket thread and push it onto the free thread stack.
     * Override this method to supply the correct socket thread.
     * @return The new socket thread.
     */  
    public SocketThread createNewThread(String strThreadType)
    {
        return null;
    }
    /**
     * Get a free socket thread or create one if there are not enough free threads.
     * This method blocks when there are not more free threads.
     * This is synchronized to synchronize access to the thread stacks.
     * @return The new socket thread.
     */
    public synchronized BaseThread getFreeThread(String strThreadType, boolean bInbound)
    {
        BaseThread socketThread = null;
        try {
            socketThread = (BaseThread)m_freeThreads.pop();
        } catch (EmptyStackException ex)    {
            socketThread = null;
            if (m_activeThreads.size() < m_iMaxThreadCount)
            {       // Create new threads
                for (int i = 0; ((i < m_iThreadPoolIncrement) && (m_activeThreads.size() < m_iMaxThreadCount)); i++)
                {
                    socketThread = this.setupNewThread(strThreadType);
                    m_activeThreads.add(socketThread);  // Technically it is active until it hits wait (which it can't call until it is released from my removeActive call when I wait)
                }
            }
            socketThread = null;
            // At max threads, wait for one to complete (or a new thread to be "removed").
            m_bWaiting = true;
            try {
                this.wait();
            } catch (InterruptedException ex2)   {
                ex2.printStackTrace();
            }
            m_bWaiting = false;
            try {
                socketThread = (BaseThread)m_freeThreads.pop();
            } catch (EmptyStackException ex3)    {
                ex3.printStackTrace();   // Never.
            }
        }
        m_activeThreads.add(socketThread);
        socketThread.setThreadType(bInbound);
        return socketThread;
    }
    /**
     * Stick this thread onto the free thread pool.
     * This is synchronized to synchronize access to the thread stacks.
     * @param thread The thread to add.
     */
    public synchronized void freeThisThread(BaseThread thread)
    {
        m_activeThreads.remove(thread);
        m_freeThreads.push(thread);
        if (m_bWaiting)
            this.notify();
    }
}
