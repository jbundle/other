/*
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package com.tourstudio.monitor;

import java.rmi.server.UnicastRemoteObject;

import com.tourstudio.monitor.common.ProcessMonitor;

import java.util.*;
import java.net.*;
import java.rmi.*;

/**
 * ProcessMonitorImpl.java
 */ 
public class ProcessMonitorObject extends UnicastRemoteObject
	implements RemoteProcessMonitor
{
	public String m_strName = null;
	public double m_dCurrent = 0.0;
	public double m_dMaximum = 1.0;

	/*
	 * Empty constructor required by RMI 
	 */
	public ProcessMonitorObject() throws java.rmi.RemoteException
	{
		super();
	}
	/*
	 * Empty constructor required by RMI 
	 */
	public ProcessMonitorObject(String strName) throws java.rmi.RemoteException
	{
		this();
		this.init(strName, 1.0);
	}
	/*
	 * Empty constructor required by RMI 
	 */
	public void init(String strName, double dMaximum)
	{
		this.setName(strName);
		this.setMaximumLoad(dMaximum);
	}
	/*
	 * Get the name of this process monitor.
	 * If you don't supply a name, the machine name is returned..
	 */
	public String getName() throws java.rmi.RemoteException
	{
		if (m_strName == null)
		{
			try	{
				InetAddress address = InetAddress.getLocalHost();
				if (address != null)
					m_strName = address.getHostName();
			} catch (java.net.UnknownHostException ex)	{
				// Ignore this error
			}
		}
		return m_strName;
	}
	/*
	 * Get the current load.
	 * If you arn't supplying special data, just return the utilization percentage.
	 */
	public double getCurrentLoad() throws java.rmi.RemoteException
	{
		return m_dCurrent;
	}
	/*
	 * Get the maximum load.
	 * By default, this is 1.0 (the getCurrentLoad should return a percentage between 0.0 and 1.0).
	 */
	public double getMaximumLoad() throws java.rmi.RemoteException
	{
		return m_dMaximum;
	}
	/*
	 * Get the name of this process monitor.
	 * By default, the machine name is returned. You may want to supply a more descriptive name.
	 */
	public void setName(String strName)
	{
		m_strName = strName;
	}
	/*
	 * Set the current load.
	 */
	public void setCurrentLoad(double dCurrent)
	{
		m_dCurrent = dCurrent;;
	}
	/*
	 * Set the maximum load.
	 */
	public void setMaximumLoad(double dMaximum)
	{
		m_dMaximum = dMaximum;
	}
} // ProcessMonitorImpl
