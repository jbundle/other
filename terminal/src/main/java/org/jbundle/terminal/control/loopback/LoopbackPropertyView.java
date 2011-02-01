/* @(#)SerialPropertyModel.java	1.5 98/07/17 SMI
 *
 */
package org.jbundle.terminal.control.loopback;

import javax.comm.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import org.jbundle.jproperties.*;
import org.jbundle.terminal.*;
import org.jbundle.terminal.control.remote.*;

/**
 * GUI element that holds the user changable elements for connection
 * configuration.
 */
public class LoopbackPropertyView extends RemoteControlPropertyView
	implements ItemListener
{
	private JLabel m_portNameLabel;
	private JCheckBox m_printChoice;

	protected LoopbackControl m_loopbackControl = null;

	public static final String PRINT_PARAM = "loopback.print";

	/**
	 * Creates and initilizes the configuration panel. The initial settings
	 * are from the parameters object.
	 */
	public LoopbackPropertyView()
	{
		super();
	}
	/**
	 * Creates and initilizes the configuration panel. The initial settings
	 * are from the parameters object.
	 */
	public LoopbackPropertyView(PropertyOwner propOwner, Properties properties)
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

		m_loopbackControl = (LoopbackControl)this.getPropertyOwner();

		panelMain.setLayout(new GridLayout(1, 2));
	
		m_portNameLabel = new JLabel("Print to System.out: ", Label.LEFT);
		panelMain.add(m_portNameLabel);

		m_printChoice = new JCheckBox();
		panelMain.add(m_printChoice);

		JPanel panelSub = this.makeNewPanel(panel, BorderLayout.SOUTH);
		super.addControlsToView(panelSub);
	}
	/**
	 * Add any listeners.
	 */
	public void addListeners()
	{
		m_printChoice.addItemListener(this);
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
		return "Loopback control properties";
	}
	/**
	 * Set the properties to the current control values.
	 */
	public void controlsToProperties()
	{	// Override this
		boolean bIsSelected = m_printChoice.isSelected();
		String strState = "false";
		if (bIsSelected)
			strState = "true";
		m_properties.setProperty(PRINT_PARAM, strState);			// Make sure all the properties are set.
		m_loopbackControl.setProperties(m_properties);
		
		super.controlsToProperties();
	}
	/**
	 * Set the controls to the current property values.
	 */
	public void propertiesToControls()
	{	// Override this
		String strPrint = m_properties.getProperty(PRINT_PARAM);
		boolean bIsSelected = false;
		if ((strPrint != null) && (strPrint.length() > 0))
			if ((strPrint.charAt(0) == 't')
			|| (strPrint.charAt(0) == 'T')
			|| (strPrint.charAt(0) == 'y')
			|| (strPrint.charAt(0) == 'Y'))
				bIsSelected = true;

		m_printChoice.setSelected(bIsSelected);
		
		super.propertiesToControls();
	}
}

