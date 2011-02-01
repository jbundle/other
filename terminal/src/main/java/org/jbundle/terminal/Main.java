/*
 * Copyright 2000 jbundle.org. All Rights Reserved,
 */
package org.jbundle.terminal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import org.jbundle.jproperties.PropertyView;
/**
 * The main program/applet.
 */
public class Main extends JApplet
	implements TerminalConstants
{
	private static final long serialVersionUID = 1L;

	protected static JApplet m_applet = null;

	protected Properties m_properties = null;
	protected BaseView m_screenView = null;
	protected JStatusBar m_status = null;
	
	public static String[] grgArgs = null;		// If this is null, this is an applet
	
	public static final String PARAM_PROPERTY_FILE = "properties";

	/**
	 * Constructor.
	 */
    public Main()
	{
		super();
    }
	/**
	 * Support for running this applet as a standalone application.
	 */
    public static void main(String[] args)
	{
        try {
			String vers = System.getProperty("java.version");
			if (vers.compareTo("1.2.0") < 0) {
				System.out.println("!!!WARNING: This program must be run with a " +
								   "1.2.0 or higher version VM!!!");
			}
			if (m_applet == null)
				m_applet = new Main();
			grgArgs = args;		// Yes, this is a standalone application
			if (args == null)
				args = new String[0];
			m_applet.init();		// Simulate the Applet call
			if ((((Main)m_applet).getProperties() == null)
				|| (!TRUE.equalsIgnoreCase(((Main)m_applet).getProperties().getProperty(NO_SCREEN))))
			{		// Usually display a screen, unless noscreen=true
				JFrame frame = new JFrame();
				frame.setTitle("Terminal");
				frame.setBackground(Color.lightGray);
				frame.getContentPane().setLayout(new BorderLayout());

				frame.getContentPane().add("Center", m_applet);
				frame.addWindowListener(
					new WindowAdapter()
					{
						public void windowClosing(WindowEvent e) {
							System.exit(0);
						}
					}
				);
				frame.pack();
				frame.setSize(500, 400);
				frame.show();
			}
			m_applet.start();		// Simulate the applet call
		} catch (Throwable t) {
			System.out.println("uncaught exception: " + t);
			t.printStackTrace();
		}
    }
	/**
	 * Get the properties member.
	 */
	public Properties getProperties()
	{
		return m_properties;
	}
    /**
     * Find the hosting frame, for the file-chooser dialog.
     */
    protected JFrame getFrame()
	{
		for (Container p = getParent(); p != null; p = p.getParent())
		{
			if (p instanceof JFrame) {
				return (JFrame) p;
			}
		}
		return null;
    }
    /**
     * Create a status bar
     */
    protected JComponent createStatusbar()
	{	// need to do something reasonable here
		m_status = new JStatusBar();
		return m_status;
    }
    /**
     * FIXME - I'm not very useful yet
     */
    class JStatusBar extends JComponent {

			public JStatusBar() {
			super();
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		}
	
			public void paint(Graphics g) {
			super.paint(g);
		}

    }
	/*
	 * Create the menu bar.
	 * @return The menu bar.
	 */
    public JMenuBar createMenuBar()
	{	// MenuBar
		JMenuBar menuBar = new JMenuBar();
		menuBar.getAccessibleContext().setAccessibleName("Swing menus");
	
		JMenuItem mi;
	
		// File Menu
		JMenu file = (JMenu) menuBar.add(new JMenu("File"));
		file.setMnemonic('F');
		file.getAccessibleContext().setAccessibleDescription("The standard 'File' application menu");
		mi = (JMenuItem) file.add(new JMenuItem("About"));
		mi.setMnemonic('t');
		mi.getAccessibleContext().setAccessibleDescription("Find out about the Terminal application");
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImageIcon groupPicture = null;	//loadImageIcon("images/Copyright.gif",
				JOptionPane.showMessageDialog(null, "Terminal - Copyright (c) tourgeek.com2000.\nAll rights reserved. DonCorley@zyan.com", "About Terminal!", JOptionPane.INFORMATION_MESSAGE);	//, groupPicture);
			}
		});

		file.addSeparator();
		mi = (JMenuItem) file.add(new JMenuItem("Preferences..."));
		mi.setMnemonic('P');
		mi.getAccessibleContext().setAccessibleDescription("Change the terminal preferences");
		file.addSeparator();
			
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				try {
					PropertyView panel = new MainPropertyView(m_screenView, m_properties);
					if (JOptionPane.showConfirmDialog(null, panel, "Terminal properties", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
					{
						panel.controlsToProperties();
						Utility.writeProperties(m_properties);
					}
					} catch (Throwable t) {
						System.out.println("uncaught exception: " + t);
						t.printStackTrace();
					}
				}
			});
				
			mi = (JMenuItem) file.add(new JMenuItem("Exit"));
			mi.setMnemonic('x');
			mi.getAccessibleContext().setAccessibleDescription("Exit the Terminal application");
			mi.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					System.exit(0);
				}
			}
		);
		return menuBar;
	}
    /**
     * APPLET INFO SUPPORT:
     *		The getAppletInfo() method returns a string describing the applet's
     * author, copyright date, or miscellaneous information.
     */
    public String getAppletInfo()
    {
		return "Name: Terminal\r\n" +
			   "Author: Don Corley\r\n" +
			   "Version 1.0.0";
    }
	/**
	 * Get Parameter Info.
	 */
	public String[][] getParameterInfo()
	{
		String rgstrReturn[][] = {
			{PARAM_PROPERTY_FILE, "", "Property file name/URL"},
			{MainPropertyView.CONTROL_PARAM, "", "Control classname"},
			{MainPropertyView.VIEW_PARAM, "", "View classname"},
			{MainPropertyView.MODEL_PARAM, "", "Model classname"}
		};		// This should be in a static, but, I know it will only be called once!
		return rgstrReturn;
	}
    /**
     * Free all the resources belonging to this applet. If all applet screens are closed, shut down the applet.
     */
    public void free()
    {
	}
    /**
     * Initializes the applet.  You never need to call this directly; it is
     * called automatically by the system once the applet is created.
     */
    public void init()
    {
		super.init();
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
	
		// create the default model, view, and control.
		m_properties = this.getTheProperties();
		m_screenView = Utility.createViewFromProperties(null, m_properties);
		BaseModel screenModel = Utility.createModelFromProperties(m_screenView, m_properties);	// Standard 24 x 80 Screen
		BaseControl screenControl = Utility.createControlFromProperties(screenModel, m_properties);
		m_screenView.setScreenControl(screenControl);

		JScrollPane scroller = new JScrollPane();
		JViewport port = scroller.getViewport();
		port.add((Component)m_screenView);
	
		contentPane.add(createMenuBar(), BorderLayout.NORTH);
		contentPane.add("Center", scroller);
		contentPane.add("South", createStatusbar());
    }
    /**
     * Called to start the applet.  You never need to call this directly; it
     * is called when the applet's document is visited.
     */
    public void start()
    {
		super.start();
    }
    /**
     * Called to stop the applet.  This is called when the applet's document is
     * no longer on the screen.  It is guaranteed to be called before destroy()
     * is called.  You never need to call this method directly
     */
    public void stop()
    {
//+		this.free();
		super.stop();
    }
    /**
     * Cleans up whatever resources are being held.  If the applet is active
     * it is stopped.
     */
    public void destroy()
    {
		super.destroy();
    }
	public Properties getTheProperties()
	{
		Properties properties = this.loadProperties();
		if ((properties.getProperty(PARAM_PROPERTY_FILE) != null)		// If a property file is specified
			|| (grgArgs != null))								// or this is a stand-alone app
				properties = Utility.getProperties(this, properties);
		else
		{
			if (properties.getProperty(MainPropertyView.CONTROL_PARAM) == null)
				properties.setProperty(MainPropertyView.CONTROL_PARAM, "Rmi");
//			properties.setProperty("model", "DataGeneral");
//			properties.setProperty("view", "Monitor");
		}
		return properties;
	}
    /**
     * Move the properties from the applet params or the application args to a property file.
     */
	public Properties loadProperties()
	{
		Properties properties = new Properties();
		if (grgArgs == null)
		{
			String[][] rgstrParamInfo = this.getParameterInfo();
			if (rgstrParamInfo != null)
			{
				for (int i = 0; i < rgstrParamInfo.length; i++)
				{
					String strParam = rgstrParamInfo[i][0];
					String strValue = this.getParameter(strParam);
					if (strValue != null)
						properties.setProperty(strParam, strValue);
				}
			}
		}
		else
		{
			for (int i = 0; i < grgArgs.length; i++)
			{
				int iEqualSign = grgArgs[i].indexOf('=');
				if (iEqualSign != -1)
				{
					String strParam = grgArgs[i].substring(0, iEqualSign);
					String strValue = grgArgs[i].substring(iEqualSign + 1);
					properties.setProperty(strParam, strValue);
				}
			}
		}
		return properties;
	}
}
