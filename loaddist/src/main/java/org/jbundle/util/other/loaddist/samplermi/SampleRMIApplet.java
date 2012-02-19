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
import java.rmi.server.*;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.util.*;
import java.awt.event.*;
import javax.naming.*;
import javax.rmi.*;

/**
 * RMI client to invoke calls through the ServletHandler
 */
public class SampleRMIApplet extends JApplet
{ 
    public static String RETURN = "\n";
    
    public static void main(String args[]) 
    {
        if (args.length != 1 ) {
            System.out.println("Usage: <hostname>");
//      	System.exit(1);
	    }
        
        JApplet applet = new SampleRMIApplet(args);
		JFrame frame;
			frame = new JFrame("RMI Client Test");
			frame.addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent e)
                {
                    System.exit(0);
                }
            });
		frame.getContentPane().add(BorderLayout.CENTER, applet);
		frame.setSize(new Dimension(640, 400));

		applet.init();		// Simulate the applet calls
		applet.start();

		frame.setVisible(true);
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
    public SampleRMIApplet()
    {
        super();
        m_bApplet = true;
    }
    public SampleRMIApplet(String[] args)
    {
        super();
        this.args = args;
        
    }
    public boolean m_bApplet = false;
    public String[] args = null;
    public JTextArea m_tf;
    public void start()
    {
        super.start();
        if ((args == null) ||
            (args.length != 2))
        {
            args = new String[2];
            if (m_bApplet)
            {
URL codebase = this.getCodeBase();
System.out.println("codebase " + codebase);
                args[0] = this.getParameter("server");
                args[1] = this.getParameter("command");
            }
            if (args[0] == null)
                args[0] = "www.tourstudio.com";
            if (args[1] == null)
                args[1] = "default";
        }

        
        this.getContentPane().add(m_tf = new JTextArea());
        
        String string = "";
        
        string += "\nserver: " + args[0] + RETURN;
        string += "\ncommand: " + args[1] + RETURN;
        
        
        if ("local".equalsIgnoreCase(args[1]))
        {
        }
        if ("remote".equalsIgnoreCase(args[1]))
        {
        try {
//            RMISocketFactory.
  //          setSocketFactory(new sun.rmi.transport.proxy.
    //                 RMIHttpToCGISocketFactory());

//            System.setSecurityManager(new RMISecurityManager());
            SampleRMI sampleRMI = null;
//111111111111111111111111111111111111111111111111
			String strRMIServer = "rmi://" + args[0] + "/" + "SampleRMI";
//			String strRMIServer = args[0] + "/" + "SampleRMI";
			string += "\nConnecting to: " + strRMIServer;
            sampleRMI = (SampleRMI) Naming.lookup(strRMIServer);
//111111111111111111111111111111111111111111111111
/* 2
        String strServer = "SampleRMI";
		Object server = null;
		try {
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
			env.put(Context.PROVIDER_URL, "rmi://" + args[0]);	// + ":1099");	// The RMI server port
			Context initial = new InitialContext(env);
			Object objref = initial.lookup(strServer);

			sampleRMI = (SampleRMI)PortableRemoteObject.narrow(objref, SampleRMI.class);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
*/ // 2
            // Invoke a single remote call to test the servlet.
            string += "\nReturn from call: "+ sampleRMI.justPass("This is a test of the RMI " + 
                              "servlet handler") + RETURN;
            string += "\nServlet installed correctly." + RETURN;

        } catch(Exception e) {
            string += "\nError: "+ e.getMessage() + RETURN;
            e.printStackTrace();
        }
        }
        
        m_tf.setText(string);
        
    }
}
