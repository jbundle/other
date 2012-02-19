/*
 * This is the main proxy program.  
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.util.other.loaddist.filter;


import java.net.*;  
import java.io.*;  
import java.util.*;

import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.spi.*;
import java.nio.charset.*;

import org.jbundle.util.other.loaddist.loaddist.*;

/**
 * A load distributor for incomming internet requests.
 */
public class FilterLoadDist extends LoadDist
{
    /**
     * Destination host name.
     */
    protected InetSocketAddress m_addrAlt = null;

    /**
     * Start this process up. 
     */
    public static void main(String[] args)
    {
        Debug.configure(args);
        if (Debug.isOutput())
            Debug.pl("Starting up");
        int iPortCount = 1;  // Minimum
        if ((args != null) && (args.length > 1))
            iPortCount = args.length;
        for (int i = 0; i < iPortCount; i++)
        {
            StringTokenizer st = new StringTokenizer(args[i], ",");
            String strSource = null;
            if (st.hasMoreElements())
                strSource = st.nextToken();
            int iSourcePort = LoadDist.getPortNo(strSource, SOURCE_PORT);
            String strSourceHostName = LoadDist.getHostName(strSource, SOURCE_HOST);
            InetSocketAddress addrSource = new InetSocketAddress(strSourceHostName, iSourcePort);
            String strDest = null;
            if (st.hasMoreElements())
                strDest = st.nextToken();
            int iDestPort = LoadDist.getPortNo(strDest, DEST_PORT);
            String strDestHostName = LoadDist.getHostName(strDest, DEST_HOST);
            InetSocketAddress addrDest = new InetSocketAddress(strDestHostName, iDestPort);
            String strAlt = null;
            if (st.hasMoreElements())
                strAlt = st.nextToken();
            int iAltPort = LoadDist.getPortNo(strAlt, DEST_PORT);
            String strAltHostName = LoadDist.getHostName(strAlt, DEST_HOST);
            InetSocketAddress addrAlt = new InetSocketAddress(strAltHostName, iAltPort);
            LoadDist mainProgram = new FilterLoadDist(addrSource, addrDest, addrAlt);
            mainProgram.start();
        }
    }
    /**
     * Constructor
     */
    public FilterLoadDist()
    {
        super();
    }
    /**
     * Constructor  
     */  
    public FilterLoadDist(InetSocketAddress addrSource, InetSocketAddress addrDest, InetSocketAddress addrAlt)
    {
        this();
        this.init(addrSource, addrDest, addrAlt);
    }
    /**
     * Initialize the program 
     */
    public void init(InetSocketAddress addrSource, InetSocketAddress addrDest, InetSocketAddress addrAlt)
    {
        super.init(addrSource, addrDest);
        m_addrAlt = addrAlt;

//x        try {
//x            m_iDestAltNetAddr = InetAddress.getByName(m_strDestAltHostName);
//x        } catch (UnknownHostException ex) {
//x            ex.printStackTrace();
//x        }
        if (Debug.isOutput())
            Debug.pl("Alt inetAddr: " + m_addrAlt);
    }
    /**
     * Create the socket thread pool.
     */
    public ThreadPool createThreadPool(int iMinThreadCount, int iMaxThreadCount, int iThreadPoolIncrement)
    {
        return new FilterSocketThreadPool(iMinThreadCount, iMaxThreadCount, iThreadPoolIncrement);
    }
    /**
     * Here is the socket, start a thread to handle it.
     * @param socket The socket to start.
     */
    public void startSocketThread(SocketChannel scClient)
    {
//        try {
            SocketChannel scServer = null;  //this.getServerSocket(null);

            SocketThread socketClientThread = (SocketThread)m_threadPool.getFreeThread(HTTP_SOCKET, INBOUND);
            SocketThread socketServerThread = null;
//            socketServerThread = (SocketThread)m_threadPool.getFreeThread(HTTP_SOCKET, OUTBOUND);
            if (Debug.isOutput())
                Debug.pl("socketThread popped");
            socketClientThread.setSocket(scClient, scServer, socketServerThread);
            ((FilterPipeThread)socketClientThread).setDestInfo(m_addrDest, m_addrAlt);
//            socketServerThread.setSocket(scServer, scClient, socketClientThread);
            synchronized(socketClientThread)
            {
                socketClientThread.notify();    // Process the thread  
            }
//            synchronized(socketServerThread)
//            {
//                socketServerThread.notify();    // Process the thread  
//            }
//        } catch (SocketException ex)    {
//            ex.printStackTrace();
//        } catch (IOException ex)    {
//            ex.printStackTrace();
//        }
    }
}
