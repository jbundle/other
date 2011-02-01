/*
 * StickyCleanup.java
 *
 * Created on March 22, 2003, 2:58 AM
 */

package org.jbundle.util.other.loaddist.sticky;

import java.util.*;

/**
 *
 * @author  don
 */
public class StickyCleanup extends Thread
{
    /**
     * Remove stale entries every this amount of time.
     */
    public static long SLEEP_TIME_MS = 1000 * 60 * 10;  // Check every ten minutes
    /**
     * An entry is stale after this amount of time (ms).
     */
    public static long ENTRY_STALE_MS = 1000 * 60 * 30; // Remove if no use in 30 minutes
    
    /**
     * Creates a new instance of StickyCleanup
     */
    public StickyCleanup()
    {
        super();
    }
    /**
     * Creates a new instance of StickyCleanup
     */
    public StickyCleanup(Object obj)
    {
        this();
        this.init(obj);
    }
    /**
     * Creates a new instance of StickyCleanup
     */
    public void init(Object obj)
    {
    }
    /**  
     * Run this thread.
     * After the set time has passed, check all the entries and remove any
     * that haven't been used for a while.
     */  
    public void run()  
    {
        while (true)
        {
            try {
                this.sleep(SLEEP_TIME_MS);
            } catch (InterruptedException ex)   {
            }
            long lTimestampOld = System.currentTimeMillis() - ENTRY_STALE_MS;
            Map mapDest = StickyLoadDist.getMapDest();
            synchronized (mapDest)
            {
                Iterator iterator = mapDest.keySet().iterator();
                while (iterator.hasNext())
                {
                    String strSourceIP = (String)iterator.next();
                    IPData ipDest = (IPData)mapDest.get(strSourceIP);
                    if (ipDest != null)
                        if (ipDest.getTimestamp() < lTimestampOld)
                    {
                            iterator.remove();
    //x                        mapDest.remove(strSourceIP);
                    }
                }
            }
        }
    }
}
