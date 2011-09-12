/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package com.tourstudio.monitor.awt;

import com.tourstudio.monitor.common.ProcessMonitor;

import java.rmi.RMISecurityManager;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.DiscoveryListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceItem;
import net.jini.lease.LeaseRenewalManager; 
import net.jini.core.lease.Lease; 
import net.jini.core.event.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.rmi.*;

/**
* TestProcessMonitor.java
*/
public class ProcessMonitorApp
	implements DiscoveryListener, RemoteEventListener
{
	public static final Dimension DEFAULT_FRAME_SIZE = new Dimension(600, 400);
	
	protected static LeaseRenewalManager leaseManager = new LeaseRenewalManager();
	
	protected JPanel m_mainPanel = null;
	
	protected JLabel m_initLabel = null;

	/*
	 * Standalone process.
	 */
	public static void main(String argv[])
	{
		new ProcessMonitorApp();

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
	public ProcessMonitorApp()
	{
		System.setSecurityManager(new RMISecurityManager());

		LookupDiscovery discover = null;
		try {
			discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
		} catch(Exception e) {
			System.err.println(e.toString());
			System.exit(1);
		}

		m_mainPanel = this.createNewWindow();
		
		discover.addDiscoveryListener(this);
	}
	/*
	 * Notified of a discovery event.
	 * See if this register has any ProcessMonitors.
	 */
	public void discovered(DiscoveryEvent evt)
	{
		ServiceRegistrar[] registrars = evt.getRegistrars();
		Class [] classes = new Class[] {ProcessMonitor.class};
		ServiceMatches matches = null;
		ProcessMonitor classifier = null;
		ServiceTemplate template = new ServiceTemplate(null, classes, null);
		
		for (int n = 0; n < registrars.length; n++) {
			System.out.println("Service found");
			ServiceRegistrar registrar = registrars[n];
			try {
				matches = registrar.lookup(template, 100);
			} catch(java.rmi.RemoteException e) {
				e.printStackTrace();
				continue;
			}
			
			boolean bListening = false;
			for (int i = 0; i < matches.items.length; i++)
			{
				ServiceItem item = matches.items[i];
				classifier = (ProcessMonitor) item.service;
				if (classifier == null) {
					System.out.println("Classifier null");
					continue;
				}

// pend(don) - I can't see to get notify to work properly - FIX THIS
bListening = true;
// pend(don) - I can't see to get notify to work properly - FIX THIS
				if (!bListening)
				{
					java.rmi.MarshalledObject handback = null;
					long leaseDuration = Lease.ANY;
					int transitions = ServiceRegistrar.TRANSITION_MATCH_NOMATCH | ServiceRegistrar.TRANSITION_NOMATCH_MATCH | ServiceRegistrar.TRANSITION_MATCH_MATCH; 
					try	{
						EventRegistration registration = registrar.notify(template,
                                transitions,
                                this,
                                handback,
                                leaseDuration);
						leaseManager.renewUntil(registration.getLease(), Lease.FOREVER, null); 
						bListening = true;
					} catch (RemoteException ex)	{
						ex.printStackTrace();
					}
				}
				// Use the service to classify a few file types
				try {
					double dCurrent;
					String strName = classifier.getName();
					double dMaximum = classifier.getMaximumLoad();
					
					JPanel panel = null;
					boolean bPieChart = false;
					if (!bPieChart)
					{	// This is a bar/history chart
						panel = new MemoryMonitor(classifier);
						((MemoryMonitor)panel).surf.start();
					}
					else
					{	// This is a pie chart
						ArcPanel arcPanel = new ArcPanel(strName, dMaximum);
						panel = new CenteredPanel(arcPanel);
						panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
						ArcUpdater process = new ArcUpdater(arcPanel, classifier);
						new Thread(process).start();
					}

					if (m_initLabel != null)
					{		// First time, get rid of the initial message.
						m_mainPanel.remove(m_initLabel.getParent());
						m_initLabel = null;
					}
					m_mainPanel.add(panel);
					this.changeGrid(m_mainPanel);

				} catch(java.rmi.RemoteException e) {
					System.err.println(e.toString());
					continue;
				}
			}
		}
	}
	/*
	 * Registrar discarded.
	 */
	public void discarded(DiscoveryEvent evt)
	{
		// empty
	}
	/*
	 * Notify me of registration of new events.
	 */
	public void notify(final net.jini.core.event.RemoteEvent p1) throws net.jini.core.event.UnknownEventException, java.rmi.RemoteException
	{
		System.out.println("Need to add code here (TestProcessMonitor.notify)");
	}
	/*
	 * Create the new window to fill with process monitors.
	 * @returns The main window to add the sub-windows to.
	 */
	public JPanel createNewWindow()
	{
		JFrame frame;
		try {
			frame = new JFrame("Process Monitor");
			frame.addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent e)
				{		// Lame
					System.exit(0);
				}
			});
		} catch (java.lang.Throwable ivjExc) {
			frame = null;
			System.out.println(ivjExc.getMessage());
			ivjExc.printStackTrace();
		}
		Dimension size = DEFAULT_FRAME_SIZE;	//aBiotest.getSize();
		if ((size == null) || ((size.getHeight() < 100) | (size.getWidth() < 100)))
			size = DEFAULT_FRAME_SIZE;
		frame.setSize(size);
		frame.setVisible(true);
		JPanel mainPanel = new JPanel();
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		
		m_initLabel = new JLabel("loading... please wait");
		JPanel initPanel = new CenteredPanel(m_initLabel);
		mainPanel.add(initPanel);

		mainPanel.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				super.componentResized(e);
				changeGrid((JPanel)e.getComponent());
			}
		});
		GridLayout layout = new GridLayout(1, 1);
		mainPanel.setLayout(layout);
		
		frame.invalidate();
		frame.validate();
		frame.repaint();
		
		return mainPanel;
	}
	/*
	 * The panel changed size or number of sub-panels, so change the grid layout so they all fit.
	 */
	public void changeGrid(JPanel mainPanel)
	{
		int iCount = mainPanel.getComponentCount();
		Rectangle bound = mainPanel.getBounds();
		double dProportion = 1.0;
		if (bound.height != 0)
			dProportion = bound.width / bound.height;
		double dWidth = Math.pow((iCount / dProportion), 0.5);
		double dHeight = dWidth * dProportion;
		int iRows = (int)Math.ceil(dWidth);
		int iColumns = (int)Math.ceil((double)iCount / (double)iRows);
		GridLayout layout = (GridLayout)mainPanel.getLayout();
		layout.setColumns(iColumns);
		layout.setRows(iRows);
		mainPanel.invalidate();
		mainPanel.validate();
		mainPanel.repaint();
	}
} // TestFileClassifier
