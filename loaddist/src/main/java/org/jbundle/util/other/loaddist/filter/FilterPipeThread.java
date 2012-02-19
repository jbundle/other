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
 * A Socket Thread is a thread that's job is to handle this socket.  
 */  
public class FilterPipeThread extends PipeThread  
{

    /**
     * Constructor.
     */
    public FilterPipeThread()  
    {  
    	super();  
    }  
    /**  
     * Constructor.  
     * @param pool My parent thread pool.
     */
    public FilterPipeThread(ThreadPool pool)
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
        super.init(pool);
    }
    /**
     * Relay the input stream to the output stream.
     * @param scIn The input channel.
     * @param scOut The output channel.
     * @param bRequest If true, this is the request from the client (So I know how to figure out the EOF).
     */
    public void relayIt(SocketChannel scIn, SocketChannel scOut, boolean bRequest) throws IOException
    {
        try {
            while (true)
            {
                dbuf.clear();
                int iDataLength = scIn.read(dbuf);
                dbuf.flip();

                if (Debug.isOutput())
                {
                    Debug.pl(" " + (bRequest ? "in: " : "out: ") + " pos: " + dbuf.position() + " limit: " + dbuf.limit() + " capacity: " + dbuf.capacity() + " data: " + iDataLength);
                    if (m_socketReverse != null)
                        if (m_iPortNo != m_addrAlt.getPort())
                            if (m_iPortNo != 0)
                                    this.printBuff(dbuf);
                }
                
                if (m_socketReverse == null)
                {
                    int iAlternatePort = this.checkBuff(dbuf);
                    if (iAlternatePort == -1)
                        continue;

                    if (Debug.isOutput())
                        Debug.pl("+++++++++++++++++++++ YIKES ++++++++++++++++++");
                    SocketChannel scServer = this.getServerSocket(null, iAlternatePort);
                    scOut = m_scOut = scServer;

                    SocketThread socketServerThread = null;
                    socketServerThread = (SocketThread)ThreadPool.getThreadPool().getFreeThread(FilterLoadDist.HTTP_SOCKET, FilterLoadDist.OUTBOUND);
                    if (Debug.isOutput())
                        Debug.pl("socketThread popped");
                    m_socketReverse = socketServerThread;
                    socketServerThread.setSocket(m_scOut, m_scIn, this);
                    synchronized(socketServerThread)
                    {
                        socketServerThread.notify();    // Process the thread  
                    }
                    if (m_iStartCurPos != 0)
                    {   // Output the characters in the buffer before continuing with this buffer.
                        if (Debug.isOutput())
                            Debug.pl("Nooooooooooooooooooooooooo writing start " + m_iStartCurPos);
                        dbuf.rewind();
                        int iLimit = dbuf.limit();
                        byte[] byBuff = new byte[iLimit];
                        // Save the current buffer
                        for (int i = 0; i < iLimit; i++)
                        {   // Set the dBuf to the chars that have to go out.
                            byBuff[i] = (byte)dbuf.get(i);  // Save this one
                        }
                        // Set the new buffer to the buffered characters
                        dbuf.clear();
                        for (int i = 0; i < m_iStartCurPos; i++)
                        {   // Set the dBuf to the chars that have to go out.
                            dbuf.put(m_byBuffer[i]);
                        }
                        dbuf.flip();
                        if (Debug.isOutput())
                        {
                            Debug.pl("ssssssssssssssssssssssssssssssssssssstart buff ssssssssssss");
                            this.printBuff(dbuf);
                            Debug.pl(" write pos: " + dbuf.position() + " limit: " + dbuf.limit() + " capacity: " + dbuf.capacity() + " data: " + iDataLength);
                        }
                        scOut.write(dbuf);
                        // Now restore the old stuff
                        dbuf.clear();
                        for (int i = 0; i < iLimit; i++)
                        {   // Restore the start of the dBuff.
                            dbuf.put(byBuff[i]);
                        }
                        dbuf.flip();
                        if (Debug.isOutput())
                        {
                            Debug.pl("fffffffffffffffffffffffffffffffffffffffffinish buff");
                            this.printBuff(dbuf);
                        }
                    }
                }

                if (iDataLength == -1)  // -1 is end of stream, 0 = no (more) data.
                    break;

                // Print the remote address and the received time
//x                dbuf.flip();
                if (Debug.isOutput())
                    Debug.pl(" write pos: " + dbuf.position() + " limit: " + dbuf.limit() + " capacity: " + dbuf.capacity() + " data: " + iDataLength);
                scOut.write(dbuf);
            }
//        } catch (TimeoutException ex)   {
//            ex.printStackTrace();
        } catch (AsynchronousCloseException ex)   {
            // Ignore
        }
    }
    public static final byte[] CONTROL = {'J', 'R','M','I', '\0', '\2'}; //, 'K'};  // "K"
    protected int m_iCurPos = 0;
    protected int m_iStartCurPos = 0;
    protected byte[] m_byBuffer = new byte[CONTROL.length];
    /**
     * Debug method - print the buffer.
     */
    public int checkBuff(ByteBuffer dbuf)
    {
        dbuf.rewind();
        
        int iPos = 0;
        int iLimit = dbuf.limit();
        int iLength = iLimit - iPos;
        if (Debug.isOutput())
            Debug.pl("******************************* len: " + iLength);
//        if (iLength < CONTROL.length)
//            return m_iDestPort;   // No match
        int i = 0;
        m_iStartCurPos = m_iCurPos;
        for (; i < iLength; i++)
        {
            if (m_iCurPos >= CONTROL.length)
                break;  // All the characters matched.
            m_byBuffer[m_iCurPos] = (byte)dbuf.get(i);
            if (CONTROL[m_iCurPos] != m_byBuffer[m_iCurPos])
            {
                if (m_iCurPos < CONTROL.length - 2)
                {       // No match - stop comparing
                    m_iCurPos = 0;
                    break;
                }
            }
            m_iCurPos++;
        }
        if (m_iCurPos != 0)
            if (m_iCurPos < CONTROL.length)
                return -1;      // Haven't checked all the chars yet, continue
        int iPort = -1;
        if (m_iCurPos == 0)
            iPort = m_addrDest.getPort();     // No match - use primary destination port
        else
        {   // Got a full match
            int byHigh = m_byBuffer[CONTROL.length - 2] & 0xff;
            int byLow = m_byBuffer[CONTROL.length - 1] & 0xff;
            iPort = (byHigh << 8) | byLow;
            if (Debug.isOutput())
                Debug.pl("Port: " + iPort + " high: " + byHigh + " low: " + byLow);
            if (iPort == 2)
                iPort = m_addrAlt.getPort();      // Match to alt port
        }
        m_iCurPos = 0;      // Reset for next time
        return iPort;    // MATCH to specified port!
    }

    public void setDestInfo(InetSocketAddress addrDest, InetSocketAddress addrAlt)
    {
        m_addrDest = addrDest;
        m_addrAlt = addrAlt;
    }
    /**
     * Get the server socket (for this client socket).
     * @param socketClient The client's socket.
     * @return The server socket.
     */
int m_iPortNo = 0;
    public SocketChannel getServerSocket(Socket socketClient, int iPortNo) throws IOException
    {
        SocketChannel scServer = null;

        InetSocketAddress isaOut = null;
m_iPortNo = iPortNo;    //x
        if (iPortNo == m_addrDest.getPort())
            isaOut = m_addrDest;
        else
            isaOut = m_addrAlt;
        scServer = SocketChannel.open();
        scServer.connect(isaOut);
        scServer.socket().setSoTimeout(LoadDist.SOCKET_TIMEOUT);

        return scServer;
    }
    /**
     * Port out.
     */
    protected InetSocketAddress m_addrDest = null;
    /**
     * Destination resolved internet address.
     */
    protected InetSocketAddress m_addrAlt = null;
}
