package com.tourstudio.monitor.common;
/**
 * ProcessMonitor.java
 */
public interface ProcessMonitor
{
	public String getName() throws java.rmi.RemoteException;

	public double getCurrentLoad() throws java.rmi.RemoteException;
    
	public double getMaximumLoad() throws java.rmi.RemoteException;
} // ProcessMonitor
