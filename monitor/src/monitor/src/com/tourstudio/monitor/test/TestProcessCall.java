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
 * TestProcessCall.java
 * This application demonstrates how to set up a callable RemoteMonitor object.
 * Just override the getCurrentLoad method.
 */ 
public class TestProcessCall extends ProcessMonitorObject
{
	/*
	 * This standalone method is used for testing.
	 */
	public static void main(String argv[])
	{
		String strName = null;
		if (argv != null) if (argv.length > 0)
			strName = argv[0];
		try	{
			ProcessMonitorObject processMonitor = new TestProcessCall(strName); // no need to keep server alive, RMI will do that
			new ProcessMonitorManager(processMonitor); // no need to keep server alive, RMI will do that
		} catch (java.rmi.RemoteException ex)	{
			ex.printStackTrace();
		}
		Object obj = new Object();
		synchronized (obj)
		{
			try	{
				obj.wait();		// Hang unit the user quits this program
			} catch (InterruptedException ex)	{
				// Ignore this
			}
		}
	}
	/*
	 * Constructor.
	 */
	public TestProcessCall(String strName) throws java.rmi.RemoteException
	{
		super(strName);
	}
	/*
	 * Get the current load.
	 * If you arn't supplying special data, just return the utilization percentage.
	 */
	private double dLastCall = 0.5;
	public double getCurrentLoad() throws java.rmi.RemoteException
	{
		return dLastCall = (Math.random() + dLastCall) / 2;
	}
} // FileClassifierServerRMI
