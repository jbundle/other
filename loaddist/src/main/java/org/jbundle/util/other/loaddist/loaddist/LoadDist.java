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
 * A load distributor for incomming internet requests.
 * NOTE: If you need more power out of this, then don't leave the socket open to the
 * client just close it like high-capacity web-servers do.
 */
public class LoadDist extends Thread
{
    /**
     * Client default port number.
     */
    public static final int SOURCE_PORT = 80;
    /**
     * Server default port number.
     */
    public static final int DEST_PORT = 8000;
    /**
     * Client default port number.
     */
    public static final String SOURCE_HOST = "www.tourstudio.com";
    /**
     * Server default port number.
     */
    public static final String DEST_HOST = "www.tourstudio.com";
    /**
     *
     */
    public static final String HTTP_SOCKET = "HTTP";
    public static final String PIPE_SOCKET = "PIPE";
    public static final boolean INBOUND = true;
    public static final boolean OUTBOUND = false;
    /**
     * The shared thread pool.
     */
    protected static ThreadPool m_threadPool = null;
    /**
     * Destination host name.
     */
    protected InetSocketAddress m_addrSource = null;
    /**
     * Destination host name.
     */
    protected InetSocketAddress m_addrDest = null;
    /**
     * Destination resolved internet address.
     */
//x    protected InetAddress m_iDestNetAddr = null;
    /**
     * The accept backlog.
     */
    public int m_iBacklog = 2;

    /**
     * The timeout (in ms) for a socket.
     */
    public static int SOCKET_TIMEOUT = 10000;

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
            LoadDist mainProgram = new LoadDist(addrSource, addrDest);
            mainProgram.start();
        }
    }
    /**
     * Given the host:port, return the host name.
     * @param strHost The host:port string.
     * @param iPortNo The default port no.
     * @return The port no.
     */
    public static int getPortNo(String strHost, int iPortNo)
    {
        if (strHost != null)
        {
            if (strHost.indexOf(':') != -1)
                strHost = strHost.substring(strHost.indexOf(':') + 1);
            try {
                iPortNo = Integer.parseInt(strHost);
            } catch (NumberFormatException ex)  {
            }
        }
        return iPortNo;
    }
    /**
     * Get the host name part of this string.
     * @param strHost The host:port string.
     * @param strHostDefault The default port host name.
     * @return The host name.
     */
    public static String getHostName(String strHost, String strHostDefault)
    {
        if (strHost != null)
            if (strHost.indexOf(':') != -1)
                strHostDefault = strHost.substring(0, strHost.indexOf(':'));
        return strHostDefault;
    }
    /**
     * Constructor
     */
    public LoadDist()
    {
        super();
    }
    /**
     * Constructor  
     */  
    public LoadDist(InetSocketAddress addrSource, InetSocketAddress addrDest)
    {
        this();
        this.init(addrSource, addrDest);
    }
    /**
     * Initialize the program 
     */
    public void init(InetSocketAddress addrSource, InetSocketAddress addrDest)
    {
        this.setupDestinationSockets(addrSource, addrDest);
        if (m_threadPool == null)
            m_threadPool = this.createThreadPool(-1, -1, -1);	    // Start up a bunch of threads 
    }
    /**
     * Setup the destination sockets.
     */
    public void setupDestinationSockets(InetSocketAddress addrSource, InetSocketAddress addrDest)
    {
        m_addrSource = addrSource;
        m_addrDest = addrDest;

        if (Debug.isOutput())
            Debug.pl("Dest inetAddr: " + m_addrDest);
    }
    /**
     * Create the socket thread pool.
     */
    public ThreadPool createThreadPool(int iMinThreadCount, int iMaxThreadCount, int iThreadPoolIncrement)
    {
        return new SocketThreadPool(iMinThreadCount, iMaxThreadCount, iThreadPoolIncrement);
    }
    /**  
     * Process the socket accepts and pass them to new threads.  
     */  
    public void run()  
    {
        try {
            // Selector for incoming time requests
            Selector acceptSelector = SelectorProvider.provider().openSelector();

            // Create a new server socket and set to non blocking mode
            ServerSocketChannel ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);

            // Bind the server socket to the local host and port
            ssc.socket().bind(m_addrSource);

            // Register accepts on the server socket with the selector. This
            // step tells the selector that the socket wants to be put on the
            // ready list when accept operations occur, so allowing multiplexed
            // non-blocking I/O to take place.
            SelectionKey acceptKey = ssc.register(acceptSelector, SelectionKey.OP_ACCEPT);

            int keysAdded = 0;

            // Here's where everything happens. The select method will
            // return when any operations registered above have occurred, the
            // thread has been interrupted, etc.
            while ((keysAdded = acceptSelector.select()) > 0) {
                // Someone is ready for I/O, get the ready keys
                Set readyKeys = acceptSelector.selectedKeys();
                Iterator i = readyKeys.iterator();

                // Walk through the ready keys collection and process date requests.
                while (i.hasNext())
                {
                    SelectionKey sk = (SelectionKey)i.next();
                    i.remove();
                    // The key indexes into the selector so you
                    // can retrieve the socket that's ready for I/O
                    ServerSocketChannel nextReady = (ServerSocketChannel)sk.channel();
                    // Accept the date request and send back the date string

                    SocketChannel socketChannel = nextReady.accept();

                    this.startSocketThread(socketChannel);
                }
            }
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * Here is the socket, start a thread to handle it.
     * @param socket The socket to start.
     */
    public void startSocketThread(SocketChannel socketChannelClient)
    {
        try {
            SocketChannel socketChannelServer = this.getServerSocket(socketChannelClient);

            SocketThread socketClientThread = (SocketThread)m_threadPool.getFreeThread(HTTP_SOCKET, INBOUND);
            SocketThread socketServerThread = (SocketThread)m_threadPool.getFreeThread(HTTP_SOCKET, OUTBOUND);
            if (Debug.isOutput())
                Debug.pl("socketThread popped");
            socketClientThread.setSocket(socketChannelClient, socketChannelServer, socketServerThread);
            socketServerThread.setSocket(socketChannelServer, socketChannelClient, socketClientThread);
            synchronized(socketClientThread)
            {
                synchronized(socketServerThread)
                {
                    socketServerThread.notify();    // Start the server thread first.
                    socketClientThread.notify();    // Start the transfer. 
                }
            }
        } catch (SocketException ex)    {
            ex.printStackTrace();
        } catch (IOException ex)    {
            ex.printStackTrace();
        }
    }
    /**
     * Get the server socket (for this client socket).
     * @param socketClient The client's socket.
     * @return The server socket.
     */
    public SocketChannel getServerSocket(SocketChannel scClient) throws IOException
    {
        SocketChannel scServer = SocketChannel.open();
        boolean bSuccess = scServer.connect(m_addrDest);
        scServer.socket().setSoTimeout(SOCKET_TIMEOUT);

        return scServer;
    }
}
