/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package com.tourstudio.monitor;

import com.tourstudio.monitor.common.ProcessMonitor; 

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
 * FileClassifierServer.java
 */ 
public class ProcessMonitorManager
	implements DiscoveryListener, LeaseListener
{
	protected ProcessMonitorObject m_processMonitor = null;

	protected LeaseRenewalManager leaseManager = new LeaseRenewalManager();
	
	/*
	 * Constructor.
	 */
	public ProcessMonitorManager()
	{
		super();
	}
	/*
	 * Constructor.
	 */
	public ProcessMonitorManager(String strName)
	{
		this();
		this.init(null, strName);
	}
	/*
	 * Constructor.
	 */
	public ProcessMonitorManager(ProcessMonitorObject processMonitor)
	{
		this();
		this.init(processMonitor, null);
	}
	/*
	 * Constructor.
	 */
	public void init(ProcessMonitorObject processMonitor, String strName)
	{ 
		try 
		{
			m_processMonitor = processMonitor;
			if (m_processMonitor == null)
				m_processMonitor = new ProcessMonitorObject(strName);
		} catch(Exception e)
		{
			System.err.println("New impl: " + e.toString());
			System.exit(1);
		} // install suitable security manager
		System.setSecurityManager(new RMISecurityManager());
		LookupDiscovery discover = null;
		try 
		{
			discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
		}
		catch(Exception e)
		{
			System.err.println(e.toString());
			System.exit(1);
		}
		discover.addDiscoveryListener(this);
	}
	/*
	 * Wait for registrar discovery events.
	 */
	public void discovered(DiscoveryEvent evt)
	{
		ServiceRegistrar[] registrars = evt.getRegistrars(); 
		for (int n = 0; n < registrars.length; n++)
		{
			ServiceRegistrar registrar = registrars[n]; // export the proxy service 
			ServiceItem item = new ServiceItem(null, m_processMonitor, null); 
			ServiceRegistration reg = null;
			try 
			{
				reg = registrar.register(item, Lease.FOREVER); 
			} catch(java.rmi.RemoteException e)
			{
				System.err.print("Register exception: "); 
				e.printStackTrace(); // System.exit(2);
				continue; 
			}
			try 
			{
				System.out.println("service registered at " + registrar.getLocator().getHost());
			} catch(Exception e)
			{
			}
			leaseManager.renewUntil(reg.getLease(), Lease.FOREVER, this);
		}
	}
	/*
	 *
	 */
	public void discarded(DiscoveryEvent evt)
	{ 
	}
	/*
	 *
	 */
	public void notify(LeaseRenewalEvent evt)
	{
		System.out.println("Lease expired " + evt.toString());
	}
	/*
	 * Get the name of this process monitor.
	 * By default, the machine name is returned. You may want to supply a more descriptive name.
	 */
	public void setName(String strName)
	{
		if (m_processMonitor != null)
			m_processMonitor.setName(strName);
	}
	/*
	 * Set the current load.
	 */
	public void setCurrentLoad(double dCurrent)
	{
		if (m_processMonitor != null)
			m_processMonitor.setCurrentLoad(dCurrent);
	}
	/*
	 * Set the maximum load.
	 */
	public void setMaximumLoad(double dMaximum)
	{
		if (m_processMonitor != null)
			m_processMonitor.setMaximumLoad(dMaximum);
	}
} // FileClassifierServerRMI
