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
public class SocketThread extends BaseThread  
{
    /**
     *
     */
    public static int BUFFER_BYTE_SIZE = 1024;
    /*
     * Direct byte buffer for reading.
     */
    protected ByteBuffer dbuf = ByteBuffer.allocateDirect(BUFFER_BYTE_SIZE);
    /*
     * Charset and decoder for US-ASCII.
     */
    private static Charset charset = Charset.forName("US-ASCII");
    /**
     * A decoder.
     */
    private CharsetDecoder decoder = charset.newDecoder();
    /**
     *Unknown length.
     */
    public static final int UNKNOWN = -1;
    /**
     * The client socket.
     */
    protected SocketChannel m_scIn = null;
    /**
     * The server socket.
     */
    protected SocketChannel m_scOut = null;
    /**
     * The reverse socket direction.
     */
    protected SocketThread m_socketReverse = null;

    /**
     * Constructor.
     */
    public SocketThread()  
    {  
    	super();  
    }  
    /**  
     * Constructor.  
     * @param pool My parent thread pool.
     */
    public SocketThread(ThreadPool pool)
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
     * Set all the information I will need to handle this task.
     * @param socketIn The client socket.
     * @param socketReverse The reverse direction.
     */  
    public void setSocket(SocketChannel scIn, SocketChannel scOut, SocketThread socketReverse)  
    {
    	m_scIn = scIn;
        m_scOut = scOut;
        m_socketReverse = socketReverse;
    }
    /**
     * Handle the request.
     */
    public void handleRequest()
    {
        this.handleSocketRequest();         // Now process this socket request.
        this.setSocket(null, null, null);   // Reset the information just to be careful.
    }
    /**  
     * Process socket request.
     * Pass the input payload to the destination socket and pass the 
	 * return payload back to this socket. 
	 */  
    public void handleSocketRequest()  
    {
    }
    /**
     * Relay the input stream to the output stream.
     * @param scIn The input channel.
     * @param scOut The output channel.
     * @param bRequest If true, this is the request from the client (So I know how to figure out the EOF).
     */
    public void relayIt(SocketChannel scIn, SocketChannel scOut, boolean bRequest) throws IOException
    {
        // Override this.
    }
    public org.jbundle.util.other.loaddist.display.DataPrinter dp = null;
    /**
     * Debug method - print the buffer.
     */
    public void printBuff(ByteBuffer dbuf)
    {
        if (dp == null)
            dp = new org.jbundle.util.other.loaddist.display.DataPrinter();

        // dbuf.array()
        dbuf.rewind();
//                    if (Debug.isOutput())
//                        Debug.pl("class: " + dbuf.array().length);
        int iPos = 0;
        int iLimit = dbuf.limit();
        int iLength = iLimit - iPos;
        byte[] rgByte = new byte[iLength];
        for (int i = iPos; i < iLimit; i++)
        {
            rgByte[i] = dbuf.get(i);
        }
        dp.printData(rgByte, 0, iLength);
/*        try {
            // Print the remote address and the received time
            dbuf.flip();
            decoder.reset();
            CharBuffer cb = decoder.decode(dbuf);
            StringBuffer sb = new StringBuffer(cb.toString());
            for (int i = 0; i < sb.length(); i++)
            {
//                if (!Character.isLetterOrDigit(sb.charAt(i)))
//                    sb.setCharAt(i, '?');
            }
            System.out.print("\n" + sb);
        } catch (CharacterCodingException ex)    {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
*/
    }
}
