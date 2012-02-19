/*
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 

 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.util.other.loaddist.samplermi;

import java.rmi.*;
import java.net.*;
import java.rmi.server.*;

/**
 * Remote object to receive calls forwarded from the ServletHandler.
 */
public class SampleRMIServer extends java.rmi.server.UnicastRemoteObject 
    implements SampleRMI {

    public SampleRMIServer() throws RemoteException 
    {
    }

    public String justPass(String passed) throws RemoteException
    {
        System.out.println("Passing back: " + passed);
        return "String passed to remote server: " + passed;
    }

    /**
     * You should not need to run this server from the command line.
     * The ServletHandler class creates its own instance of the
     * rmiregistry and (optionally) an instance of this class as well.
     * This main method will not be executed from the ServletHandler.  
     */
    public static void main(String args[]) {
	try {
	    System.setSecurityManager(new RMISecurityManager());

        String strHostName = "www.tourstudio.com";
        String strHostPort = strHostName;
        int iRegistry = 1098;
        if ((args != null) && (args.length > 0))
        {
            if (args[0].indexOf(':') != -1)
            {
                strHostName = args[0].substring(0, args[0].indexOf(':'));
                iRegistry = Integer.parseInt(args[0].substring(args[0].indexOf(':') + 1));
            }
            else
                strHostName = args[0];
            strHostPort = args[0];
        }

	    // create a registry if one is not running already.
	    try {
			System.out.println(" new registry on " + iRegistry + " - " + strHostName);
			java.rmi.registry.LocateRegistry.createRegistry(iRegistry);
	    } catch (java.rmi.server.ExportException ee) {
		// registry already exists, we'll just use it.
	    } catch (RemoteException re) {
			System.err.println(re.getMessage());
			re.printStackTrace();
	    }

	    String strServerName = "SampleRMI";
        SampleRMIServer app = new SampleRMIServer();
		try {
			// Bind this object instance to the name "HelloServer"
			InetAddress iNetAddr = InetAddress.getByName(strHostName);
			strHostName = iNetAddr.getHostName();
			String strName = "//" + strHostPort + "/" + strServerName;
			Naming.rebind(strName, app);
			System.out.println(strName + " bound in registry");
		} catch (Exception e) {
			System.out.println("HelloImpl err: " + e.getMessage());
			e.printStackTrace();
		}

	}catch(Exception e){
	    System.out.println("Error: "+e.getMessage());
	    e.printStackTrace();
	}
    }
    
    /**
     * Make a new Sample RMI server.
     */
    public SampleRMI makeaNewOne() throws java.rmi.RemoteException
    {
        System.out.println("Creating a new server");
        SampleRMI sample = new SampleRMIServer();
        return sample;
    }
    
}
