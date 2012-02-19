/*
 * This is the main proxy program.  
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.util.other.loaddist.sticky;

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
public class StickyLoadDist extends LoadDist
{
    protected InetSocketAddress[] m_rgaddrDest = null;

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
            InetSocketAddress[] rgaddrDest = new InetSocketAddress[0];
            while (st.hasMoreElements())
            {
                strDest = st.nextToken();
                int iDestPort = LoadDist.getPortNo(strDest, DEST_PORT);
                String strDestHostName = LoadDist.getHostName(strDest, DEST_HOST);
                int iNext = rgaddrDest.length;
                InetSocketAddress[] rgaddrDestOld = rgaddrDest;
                rgaddrDest = new InetSocketAddress[iNext + 1];
                rgaddrDest[iNext] = new InetSocketAddress(strDestHostName, iDestPort); 
                while (iNext > 0)
                {
                    iNext--;
                    rgaddrDest[iNext] = rgaddrDestOld[iNext];
                }
            }
            LoadDist mainProgram = new StickyLoadDist(addrSource, rgaddrDest);
            mainProgram.start();
        }
        StickyCleanup cleanUp = new StickyCleanup(null);
        cleanUp.start();
    }
    /**
     * Constructor
     */
    public StickyLoadDist()
    {
        super();
    }
    /**
     * Constructor  
     */  
    public StickyLoadDist(InetSocketAddress addrSource, InetSocketAddress[] rgaddrDest)
    {
        this();
        this.init(addrSource, rgaddrDest);
    }
    /**
     * Initialize the program 
     */
    public void init(InetSocketAddress addrSource, InetSocketAddress[] rgaddrDest)
    {
        super.init(addrSource, null);
        m_rgaddrDest = rgaddrDest;
    }
    /**
     * Get the server socket (for this client socket).
     * @param socketClient The client's socket.
     * @return The server socket.
     */
    public SocketChannel getServerSocket(SocketChannel scClient) throws IOException
    {
        Socket socketClient = scClient.socket();
        String strSourceIP = socketClient.getInetAddress().getHostAddress();
        
        InetSocketAddress isaOut = this.getServerSocketAddress(strSourceIP);
        SocketChannel scServer = SocketChannel.open();
        boolean bSuccess = scServer.connect(isaOut);
        scServer.socket().setSoTimeout(SOCKET_TIMEOUT);

        return scServer;
    }
    /**
     * The list of addresses.
     * Note: that Hashtable is synchronized.
     */
    protected static Map m_mapDest = new Hashtable();
    /**
     *
     */
    public static Map getMapDest()
    {
        return m_mapDest;
    }
    /**
     * From the source IP, look up the destination IP address.
     */
    public InetSocketAddress getServerSocketAddress(String strSourceIP)
    {
        IPData ipDest = (IPData)m_mapDest.get(strSourceIP);
        if (ipDest == null)
        {
            synchronized (m_mapDest)
            {
                ipDest = (IPData)m_mapDest.get(strSourceIP);
                if (ipDest == null)
                {
                    int iDestIndex = this.getNextServerIndex();
                    ipDest = new IPData(iDestIndex, 0);
                    m_mapDest.put(strSourceIP, ipDest);
                }
            }
        }
        long lTimestamp = System.currentTimeMillis();
        ipDest.setTimestamp(lTimestamp);
        InetSocketAddress isaOut = m_rgaddrDest[ipDest.getIndex()];
        return isaOut;
    }
    /**
     * From the source IP, look up the destination IP address.
     * Note: Since this is only called from getServerSocketAddress (which is synchronized)
     * there are no concurrency issues.
     * @return The index of the next server.
     */
    public int getNextServerIndex()
    {
        int iDestIndex = m_iNextAddress;
        m_iNextAddress++;
        if (m_iNextAddress >= m_rgaddrDest.length)
            m_iNextAddress = 0;
        return iDestIndex;
    }
    /**
     * For a round-robin assignment, the next index.
     */
    protected static int m_iNextAddress = 0;
}
