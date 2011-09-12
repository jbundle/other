/*
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.util.other.loaddist.samplermi;

import java.rmi.*;
import java.rmi.server.*;

/**
 * RMI client to invoke calls through the ServletHandler
 */
public class SampleRMIClient { 
    public static void main(String args[]) {
	try {
	    if (args.length != 1 ) {
		System.out.println("Usage: <hostname>");
		System.exit(1);
	    }
	    

	    /*
	     * NOTICE: To make this example easier to set-up and run,
	     * the following call causes RMI to use a socket factory
	     * that is only capable of invoking remote methods over
	     * HTTP to a CGI script (or servlet).  This client
	     * simulates the behavior that an RMI client would have if
	     * it were forced to invoke remote calls on a server that
	     * resided outside a local firewall.
	     *
	     * It is not recommended that you make use of this sun
	     * implementation class (or any sun.* class) in general-
	     * purpose applications for the following reasons:
	     *
	     *   - Sun Microsystem's does not support the use of 
             *     sun.* classes.
	     *   - All sun.* classes are specific to Sun Microsystem's
	     *     implementation of the Java Development Kit.
	     *       
	     * To fully test the example, you will need to comment out
	     * the following line of code, ensure that the client and
	     * server are on opposite sides of a firewall and set the
	     * client VM's proxy host properties as follows:
	     *
	     *   java -Dhttp.proxyHost=<proxyHost> -Dhttp.proxyPort=<proxyPort> 
	     *       samplermi.SampleRMIClient <servletHostname> 
	     */
//	    RMISocketFactory.setSocketFactory(new sun.rmi.transport.proxy.RMIHttpToCGISocketFactory());

	    System.setSecurityManager(new RMISecurityManager());
	    SampleRMI sampleRMI = (SampleRMI) Naming.lookup("rmi://" + args[0] + "/SampleRMI");

	    // Invoke a single remote call to test the servlet.
	    System.out.println(sampleRMI.justPass("This is a test of the RMI servlet handler"));
/*
        SampleRMI sample2 = sampleRMI.makeaNewOne();
        System.out.println("Pausing for a few secs");
        Thread.currentThread().sleep(1000 * 4);
        System.out.println(sample2.justPass("This is the second RMI handler"));
*/
	    System.out.println("Servlet installed correctly.");
	    System.exit(0);
	    
	} catch(Exception e) {
	    System.err.println("Error: "+e.getMessage());
	    e.printStackTrace();
	}
    }
}
