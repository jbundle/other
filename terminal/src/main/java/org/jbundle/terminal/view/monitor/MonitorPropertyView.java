/* @(#)SerialPropertyModel.java	1.5 98/07/17 SMI
 * Copyright (c) 2000 jbundle.org. All Rights Reserved.
 *		don@tourgeek.com
 */
package org.jbundle.terminal.view.monitor;

import javax.comm.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import org.jbundle.jproperties.*;
import org.jbundle.terminal.*;
import org.jbundle.terminal.view.*;
import org.jbundle.terminal.view.remote.*;

/**
 * GUI element that holds the user changable elements for connection
 * configuration.
 */
public class MonitorPropertyView extends RemoteViewPropertyView
	implements ItemListener
{
	private JLabel m_fontSizeLabel;
	private JComboBox m_fontSizeChoice;

	private static final String[] m_rgstrSize = {
		"10",
		"12",
		"14",
		"18"};

	protected MonitorView m_monitorView = null;

	public static final String FONT_SIZE_PARAM = "monitor.fontsize";

	/**
	 * Creates and initilizes the configuration panel. The initial settings
	 * are from the parameters object.
	 */
	public MonitorPropertyView()
	{
		super();
	}
	/**
	 * Creates and initilizes the configuration panel. The initial settings
	 * are from the parameters object.
	 */
	public MonitorPropertyView(PropertyOwner propOwner, Properties properties)
	{
		this();
		this.init(propOwner, properties);
	}
	/**
	 * Creates and initilizes the configuration panel. The initial settings
	 * are from the parameters object.
	 */
	public void init(PropertyOwner propOwner, Properties properties)
	{
		super.init(propOwner, properties);
	}
	/**
	 * Add your property controls to this panel.
	 * Remember to set your own layout manager.
	 * Also, remember to create a new JPanel, and pass it to the super class
	 * so controls of the superclass can be included.
	 * You have a 3 x 3 grid, so add three columns for each control
	 * @param panel This is the panel to add your controls to.
	 */
	public void addControlsToView(JPanel panel)
	{
		panel.setLayout(new BorderLayout());
		JPanel panelMain = this.makeNewPanel(panel, BorderLayout.CENTER);

		m_monitorView = (MonitorView)this.getPropertyOwner();

		panelMain.setLayout(new GridLayout(1, 2));
	
		m_fontSizeLabel = new JLabel("Font size: ", Label.LEFT);
		panelMain.add(m_fontSizeLabel);

		String strSize = m_properties.getProperty(FONT_SIZE_PARAM);
		if (strSize == null)
			strSize = m_rgstrSize[1];	// 12
		panelMain.add(m_fontSizeChoice = (JComboBox)this.makeControlPopup(m_rgstrSize, strSize));

		JPanel panelSub = this.makeNewPanel(panel, BorderLayout.SOUTH);
		super.addControlsToView(panelSub);
	}
	/**
	 * Add any listeners.
	 */
	public void addListeners()
	{
		m_fontSizeChoice.addItemListener(this);
		super.addListeners();
	}
	/**
	 * Event handler for changes in the current selection of the Choices.
	 * If a port is open the port can not be changed.
	 * If the choice is unsupported on the platform then the user will
	 * be notified and the settings will revert to their pre-selection
	 * state.
	 */
	public void itemStateChanged(ItemEvent e)
	{
		super.itemStateChanged(e);	// Don't do anything until they hit okay (see controlsToProperties).
	}
	/**
	 * Get the description of this option panel.
	 */
	public String getDescription()
	{
		return "Monitor control properties";
	}
	/**
	 * Set the properties to the current control values.
	 */
	public void controlsToProperties()
	{	// Override this
		String strSelection = (String)m_fontSizeChoice.getSelectedItem();
		String strCurrent = m_properties.getProperty(FONT_SIZE_PARAM);
		if (strSelection != null)
			if (!strSelection.equals(strCurrent))
				m_properties.setProperty(FONT_SIZE_PARAM, strSelection);
		
		super.controlsToProperties();
	}
	/**
	 * Set the controls to the current property values.
	 */
	public void propertiesToControls()
	{	// Override this
		m_fontSizeChoice.setSelectedItem(m_properties.getProperty(FONT_SIZE_PARAM));
	
		super.propertiesToControls();
	}
}
