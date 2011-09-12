/*
 * This is the main proxy program.  
 * Copyright © 2011 jbundle.org. All rights reserved.
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
 * Class to hold the data on the current IP connection.
 */
public class IPData
{
    protected int m_iIndex;
    protected long m_lTimestamp;
    /**
     * Constructor
     */
    public IPData(int iIndex, long lTimestamp)
    {
        m_iIndex = iIndex;
        m_lTimestamp = lTimestamp;
    }
    /**
     * Get the index.
     * @return The index.
     */
    public int getIndex()
    {
        return m_iIndex;
    }
    /**
     *
     */
    public long getTimestamp()
    {
        return m_lTimestamp;
    }
    /**
     *
     */
    public void setTimestamp(long lTimestamp)
    {
        m_lTimestamp = lTimestamp;
    }
}
