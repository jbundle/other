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
public class PipeThread extends SocketThread  
{
    /**
     * Total data moved through this pipe.
     */
    protected long m_lTotalData = 0;

    /**
     * Constructor.
     */
    public PipeThread()  
    {  
    	super();  
    }  
    /**  
     * Constructor.  
     * @param pool My parent thread pool.
     */
    public PipeThread(ThreadPool pool)
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
     * Process socket request.
     * Pass the input payload to the destination socket and pass the 
	 * return payload back to this socket. 
	 */  
    public void handleSocketRequest()  
    {
        try {
            this.relayIt(m_scIn, m_scOut, m_bInbound);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally   {
            // It is all done, close out
            try {
                if (Debug.isOutput())
                    Debug.pl("shutting down input thread" + " inconnect: " + m_scIn.isConnected() + " inpending: " + m_scIn.isConnectionPending() + " outconnect: " + m_scOut.isConnected() + " outpending: " + m_scOut.isConnectionPending());
                m_scOut.socket().shutdownInput();    // This will cause the reverse-thread to stop.
            } catch (SocketException ex) {
                // Typically ignore SocketErrors such as: Transport endpoint is not connected.
                if (Debug.isOutput())
                    ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    m_scOut.close();
                } catch (IOException ex) {
                    // Ignore.
                }
            }
        }
    }
    /**
     * Relay the input stream to the output stream.
     * @param scIn The input channel.
     * @param scOut The output channel.
     * @param bRequest If true, this is the request from the client (So I know how to figure out the EOF).
     */
    public void relayIt(SocketChannel scIn, SocketChannel scOut, boolean bInbound) throws IOException
    {
        try {
            while (true)
            {
                dbuf.clear();
                int iDataLength = scIn.read(dbuf);
                dbuf.flip();

                if (Debug.isOutput())
                {
                    Debug.pl(" " + (bInbound ? "in: " : "out: ") + " pos: " + dbuf.position() + " limit: " + dbuf.limit() + " capacity: " + dbuf.capacity() + " data: " + iDataLength + " connect: " + m_scOut.isConnected() + " pending: " + m_scOut.isConnectionPending());
                    if (Debug.isOutput(Debug.PRINT_DATA))
                        this.printBuff(dbuf);
                }

                if (iDataLength == -1)  // -1 is end of stream, 0 = no (more) data.
                    break;
                m_lTotalData += iDataLength;

                // Print the remote address and the received time
                scOut.write(dbuf);
            }
//        } catch (TimeoutException ex)   {
//            ex.printStackTrace();
        } catch (AsynchronousCloseException ex)   {
            // Ignore
        } catch (IOException ex) {
            // Typically ignore I/O Errors such as: Connection reset by peer.
            if (Debug.isOutput())
                ex.printStackTrace();
        }
    }
}
