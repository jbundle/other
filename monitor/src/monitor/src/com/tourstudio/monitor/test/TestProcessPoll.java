/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package com.tourstudio.monitor.test;

import com.tourstudio.monitor.common.ProcessMonitor; 
import com.tourstudio.monitor.*; 

import net.jini.discovery.LookupDiscovery; 
import net.jini.discovery.DiscoveryListener; 
import net.jini.discovery.DiscoveryEvent; 
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistration;
import net.jini.core.lease.Lease;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lease.LeaseListener;
import net.jini.lease.LeaseRenewalEvent;
import java.rmi.RMISecurityManager;
/**
 * TestProcessPoll.java
 * This application demonstrates how to set up a pollable RemoteMonitor object.
 */ 
public class TestProcessPoll implements Runnable
{
	protected ProcessMonitorManager m_process = null;

	/*
	 * This standalone method is used for testing.
	 */
	public static void main(String argv[])
	{
		String strName = null;
		if (argv != null) if (argv.length > 0)
			strName = argv[0];
		new Thread(new TestProcessPoll(strName)).start(); // no need to keep server alive, RMI will do that
	}
	/*
	 * Constructor.
	 */
	public TestProcessPoll(String strName)
	{
		super();
		
		m_process = new ProcessMonitorManager(strName); // no need to keep server alive, RMI will do that
	}
	public void run()
	{
		while (true)
		{
			m_process.setCurrentLoad(Math.random());
			try	{
				Thread.currentThread().sleep(1 * 1000);
			} catch (InterruptedException ex)	{
				System.exit(0);
			}
		}
	}
} // FileClassifierServerRMI
