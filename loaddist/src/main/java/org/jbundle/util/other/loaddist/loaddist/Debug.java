/*
 * This is the main proxy program.  
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.util.other.loaddist.loaddist;

import java.io.*;

/**  
 * Debug utilities.  
 */  
public class Debug extends Object
{
    /**
     * If true, send output to System.out
     */
    public static boolean gOutput = false;
    /**
     *
     */
    public static final String PRINT_DATA = "PRINT_DATA";
    
    /**
     * Configure the Debug logger. 
     */
    public static void configure(String[] args)
    {
        if (false)
        {       // Specify an output stream
            try {
                PrintStream out = new PrintStream(new FileOutputStream("loaddistout.txt"));
                System.setOut(out);
                out = new PrintStream(new FileOutputStream("loaddisterr.txt"));
                System.setErr(out);
                gOutput = true;
            } catch (FileNotFoundException ex)  {
            }
        }
        else if (gOutput)
        {   // Use standard out.
            gOutput = true;
        }
        else
        {   // Default - no output.
//            gOutput = false;
        }
    }
    /**
     * Do I output?
     */
    public static boolean isOutput(String strType)
    {
        if (PRINT_DATA == strType)
            return false;
        return Debug.isOutput();
    }
    /**
     * Do I output?
     */
    public static boolean isOutput()
    {
        return gOutput;
    }
    /**
     * Print this line.
     * @param string The string to print.
     */
    public static void pl(String string)
    {
        if (gOutput)
            System.out.println(string);
    }
}

