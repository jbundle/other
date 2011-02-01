/*
 * ArcUpdater.java
 *
 * Created on January 25, 2001, 11:47 PM
 */
 
package com.tourstudio.monitor.awt;

import java.rmi.*;

import com.tourstudio.monitor.common.*;

/** 
 * ArcUpdater - Update the PieChart at the pre-set intervals.
 * @author  Administrator
 * @version 
 */
public class ArcUpdater extends Object
	implements Runnable
{
	public static final int DELAY = 3 * 1000;	// 3 Second delay (default)
	protected int m_iDelay = DELAY;

	protected ArcPanel m_arcPanel = null;
	protected ProcessMonitor m_processMonitor = null;

	/*
	 * Creates new ArcUpdater
	 */
	public ArcUpdater()
	{
		super();
	}
	/*
	 * Creates new ArcUpdater
	 */
	public ArcUpdater(ArcPanel arcPanel, ProcessMonitor processMonitor)
	{
		this();
		this.init(arcPanel, processMonitor);
	}
	/*
	 * Creates new ArcUpdater
	 */
	public void init(ArcPanel arcPanel, ProcessMonitor processMonitor)
	{
		m_arcPanel = arcPanel;
		m_processMonitor = processMonitor;
	}
	/*
	 * Run this task.
	 */
	public void run()
	{
		while ((m_arcPanel != null)
			&& (m_processMonitor != null))
		{
			try	{
				double dCurrentLoad = m_processMonitor.getCurrentLoad();
				m_arcPanel.setPercentage(dCurrentLoad);
			} catch (RemoteException ex)	{
			}
			try	{
				Thread.currentThread().sleep(m_iDelay);
			} catch (InterruptedException ex)	{
				// Ignore
			}
		}
	}
}