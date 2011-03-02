/* @(#)SerialPropertyModel.java	1.5 98/07/17 SMI
 *
 */
package org.jbundle.terminal.control.rmi;

import javax.comm.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import org.jbundle.util.apprunner.*;
import org.jbundle.terminal.*;
import org.jbundle.terminal.view.*;
/**
 * GUI element that holds the user changable elements for connection
 * configuration.
 */
public class RmiPropertyView extends PropertyView
	implements ItemListener, ActionListener
{
	private JLabel m_serverNameLabel = null;
	private JTextField m_serverTextField = null;

	protected JComboBox m_comboControl = null;
	protected JButton m_buttonControl = null;

	public static final String[] m_rgstrControls = {
		"(Use remote properties)",
		"Serial",
		"Rmi",
		"Loopback"};

	protected RmiControl m_rmiControl = null;

	public static final String SERVER_PARAM = "rmi.server";
	public static final String SERVER_CONTROL_PARAM = "rmi.control";

	/**
	 * Creates and initilizes the configuration panel. The initial settings
	 * are from the parameters object.
	 */
	public RmiPropertyView()
	{
		super();
	}
	/**
	 * Creates and initilizes the configuration panel. The initial settings
	 * are from the parameters object.
	 */
	public RmiPropertyView(PropertyOwner propOwner, Properties properties)
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
	/*
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

		m_rmiControl = (RmiControl)this.getPropertyOwner();

		panelMain.setLayout(new GridLayout(2, 3));
	
		m_serverNameLabel = new JLabel("Server name: ", Label.LEFT);
		panelMain.add(m_serverNameLabel);

		m_serverTextField = new JTextField();
		panelMain.add(m_serverTextField);
		panelMain.add(new JLabel(""));

		String strControl = m_properties.getProperty(SERVER_CONTROL_PARAM);
		if (strControl == null)
			strControl = m_rgstrControls[0];
		panelMain.add(new JLabel("Control:", JLabel.LEFT));
		panelMain.add(m_comboControl = (JComboBox)this.makeControlPopup(m_rgstrControls, strControl));
		panelMain.add(m_buttonControl = (JButton)new JButton("Change settings..."));

		JPanel panelSub = this.makeNewPanel(panel, BorderLayout.SOUTH);
		super.addControlsToView(panelSub);
	}
	/**
	 * Add any listeners.
	 */
	public void addListeners()
	{
		m_buttonControl.addActionListener(this);
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
		String strTextField = m_serverTextField.getText();
		if ((strTextField == null) || (strTextField.length() == 0))
			strTextField = "[codebase]";
		m_properties.setProperty(SERVER_PARAM, strTextField);			// Make sure all the properties are set.

		String strSelection = (String)m_comboControl.getSelectedItem();
		String strCurrent = m_properties.getProperty(SERVER_CONTROL_PARAM);
		if (!strSelection.equals(strCurrent))
			m_properties.setProperty(SERVER_CONTROL_PARAM, strSelection);

		m_rmiControl.setProperties(m_properties);
		
		super.controlsToProperties();
	}
	/**
	 * Set the controls to the current property values.
	 */
	public void propertiesToControls()
	{	// Override this
		String strTextField = m_properties.getProperty(SERVER_PARAM);
		if (strTextField == null)
			strTextField = "[codebase]";
		m_serverTextField.setText(strTextField);

		m_comboControl.setSelectedItem(m_properties.getProperty(SERVER_CONTROL_PARAM));
		
		super.propertiesToControls();
	}
	/**
	 * User pressed a button.
	 */
	public void actionPerformed(ActionEvent e)
	{
		PropertyOwner propOwner = null;
		if (e.getSource() == m_buttonControl)
			propOwner = null;//+((BaseView)m_propOwner).getScreenControl();
		if (propOwner != null)
		{
			this.controlsToProperties();
			PropertyView panel = propOwner.getPropertyView(m_properties);
			if (panel == null)	// Default
				panel = new PropertyView(m_propOwner, m_properties);
			if (JOptionPane.showConfirmDialog(null, panel, panel.getDescription(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
			{
				panel.controlsToProperties();
				propOwner.setProperties(m_properties);	// Send the property owner the new settings
			}
		}
//+		super.actionPerformed(e);
	}
}
