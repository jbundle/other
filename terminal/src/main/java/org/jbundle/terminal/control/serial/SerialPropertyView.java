/*
 *		don@tourgeek.com
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.terminal.control.serial;

import javax.comm.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import org.jbundle.util.apprunner.*;
import org.jbundle.terminal.*;
import org.jbundle.terminal.control.remote.*;

/**
 * GUI element that holds the user changable elements for connection
 * configuration.
 */
public class SerialPropertyView extends RemoteControlPropertyView
	implements ItemListener
{
	private JLabel portNameLabel;
	private JComboBox portChoice;
	
	private JLabel baudLabel;
	private JComboBox baudChoice;
	
	private JLabel flowControlInLabel;
	private JComboBox flowChoiceIn;
	
	private JLabel flowControlOutLabel;
	private JComboBox flowChoiceOut;
	
	private JLabel databitsLabel;
	private JComboBox databitsChoice;

	private JLabel stopbitsLabel;
	private JComboBox stopbitsChoice;

	private JLabel parityLabel;
	private JComboBox parityChoice;

	protected SerialControl m_serialControl = null;

	public static final String PORT_PARAM = "serial.port";
	public static final String BAUD_PARAM = "serial.baud";
	public static final String FLOWCONTROLIN_PARAM = "serial.flowcontrolin";
	public static final String FLOWCONTROLOUT_PARAM = "serial.flowcontrolout";
	public static final String DATABITS_PARAM = "serial.databits";
	public static final String STOPBITS_PARAM = "serial.stopbits";
	public static final String PARITY_PARAM = "serial.parity";

	private static final String[] m_rgstrBauds = {
		"300",
		"2400",
		"9600",
		"14400",
		"28800",
		"38400",
		"57600",
		"152000"};
	private static final String[] m_rgstrFlowControlIn = {
		"None",
		"Xon/Xoff In",
		"RTS/CTS In"};
	private static final String[] m_rgstrFlowControlOut = {
		"None",
		"Xon/Xoff Out",
		"RTS/CTS Out"};
	private static final String[] m_rgstrDataBits = {
		"5",
		"6",
		"7",
		"8"};
	private static final String[] m_rgstrStopBits = {
		"1",
		"1.5",
		"2"};
	private static final String[] m_rgstrParity = {
		"None",
		"Even",
		"Odd"};
	/**
	 * Creates and initilizes the configuration panel. The initial settings
	 * are from the parameters object.
	 */
	public SerialPropertyView()
	{
		super();
	}
	/**
	 * Creates and initilizes the configuration panel. The initial settings
	 * are from the parameters object.
	 */
	public SerialPropertyView(PropertyOwner propOwner, Properties properties)
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

		this.m_serialControl = (SerialControl)this.getPropertyOwner();
		SerialPropertyModel parameters = m_serialControl.getParameters();
		parameters.getProperties(m_properties);			// Make sure all the properties are set.

		panelMain.setLayout(new GridLayout(4, 4));
	
		portNameLabel = new JLabel("Port Name:", Label.LEFT);
		panelMain.add(portNameLabel);

		portChoice = new JComboBox();
		panelMain.add(portChoice);
		this.listPortChoices(parameters.getPortName());
		
		baudLabel = new JLabel("Baud Rate:", Label.LEFT);
		panelMain.add(baudLabel);
	
		baudChoice = new JComboBox();
		this.addItems(baudChoice, m_rgstrBauds, Integer.toString(parameters.getBaudRate()));
		panelMain.add(baudChoice);
	
		flowControlInLabel = new JLabel("Flow Control In:", Label.LEFT);
		panelMain.add(flowControlInLabel);
	
		flowChoiceIn = new JComboBox();
		this.addItems(flowChoiceIn, m_rgstrFlowControlIn, parameters.getFlowControlInString());
		panelMain.add(flowChoiceIn);
	
		flowControlOutLabel = new JLabel("Flow Control Out:", Label.LEFT);
		panelMain.add(flowControlOutLabel);
	
		flowChoiceOut = new JComboBox();
		this.addItems(flowChoiceOut, m_rgstrFlowControlOut, parameters.getFlowControlOutString());
		panelMain.add(flowChoiceOut);
	
		databitsLabel = new JLabel("Data Bits:", Label.LEFT);
		panelMain.add(databitsLabel);
	
		databitsChoice = new JComboBox();
		this.addItems(databitsChoice, m_rgstrDataBits, parameters.getDatabitsString());
		panelMain.add(databitsChoice);
	
		stopbitsLabel = new JLabel("Stop Bits:", Label.LEFT);
		panelMain.add(stopbitsLabel);
	
		stopbitsChoice = new JComboBox();
		this.addItems(stopbitsChoice, m_rgstrStopBits, parameters.getStopbitsString());
		panelMain.add(stopbitsChoice);
	
		parityLabel = new JLabel("Parity:", Label.LEFT);
		panelMain.add(parityLabel);
		
		parityChoice = new JComboBox();
//?		parityChoice.setSelectedItem("None");
		this.addItems(parityChoice, m_rgstrParity, parameters.getParityString());
		panelMain.add(parityChoice);

		JPanel panelSub = this.makeNewPanel(panel, BorderLayout.SOUTH);
		super.addControlsToView(panelSub);
	}
	/**
	 * Add any listeners.
	 */
	public void addListeners()
	{
		portChoice.addItemListener(this);
		baudChoice.addItemListener(this);
		flowChoiceIn.addItemListener(this);
		flowChoiceOut.addItemListener(this);
		databitsChoice.addItemListener(this);
		stopbitsChoice.addItemListener(this);
		parityChoice.addItemListener(this);
		super.addListeners();
	}
	/**
	 * Sets the elements for the portChoice from the ports available on the
	 * system. Uses an emuneration of comm ports returned by 
	 * CommPortIdentifier.getPortIdentifiers(), then sets the current
	 * choice to a mathing element in the parameters object.
	 */
	public void listPortChoices(String strDefaultPort)
	{
		CommPortIdentifier portId;

		Enumeration en = CommPortIdentifier.getPortIdentifiers();

		// iterate through the ports.
		while (en.hasMoreElements())
		{
			portId = (CommPortIdentifier) en.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL)
			{
				String strPortName = portId.getName();
				if (strPortName.equalsIgnoreCase(strDefaultPort))
					strDefaultPort = strPortName;
				portChoice.addItem(strPortName);
			}
		}
		portChoice.setSelectedItem(strDefaultPort);
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
		// Don't do anything until they hit okay (see controlsToProperties).
	}
	/**
	 * Get the description of this option panel.
	 */
	public String getDescription()
	{
		return "Serial port properties";
	}
	/**
	 * Set the properties to the current control values.
	 */
	public void controlsToProperties()
	{	// Override this
		SerialPropertyModel parameters = m_serialControl.getParameters();

		parameters.setPortName((String)portChoice.getSelectedItem());
		parameters.setBaudRate((String)baudChoice.getSelectedItem());
		parameters.setFlowControlIn((String)flowChoiceIn.getSelectedItem());
		parameters.setFlowControlOut((String)flowChoiceOut.getSelectedItem());
		parameters.setDatabits((String)databitsChoice.getSelectedItem());
		parameters.setStopbits((String)stopbitsChoice.getSelectedItem());
		parameters.setParity((String)parityChoice.getSelectedItem());

		try	{
			m_serialControl.closeConnection();	// Set new params on the port
			m_serialControl.openConnection();
		} catch (SerialControlException ex)	{
			try	{	// The params were reset, re-open
				m_serialControl.closeConnection();	// Set new params on the port
				m_serialControl.openConnection();
			} catch (SerialControlException ex2)	{
			}
			JOptionPane.showMessageDialog(this, "Error in parameters - " + ex.getMessage());
		}

		parameters.getProperties(m_properties);			// Make sure all the properties are set.
		
		super.controlsToProperties();
	}
	/**
	 * Set the controls to the current property values.
	 */
	public void propertiesToControls()
	{	// Override this
		SerialPropertyModel parameters = m_serialControl.getParameters();

		portChoice.setSelectedItem(parameters.getPortName());
		baudChoice.setSelectedItem(parameters.getBaudRateString());
		flowChoiceIn.setSelectedItem(parameters.getFlowControlInString());
		flowChoiceOut.setSelectedItem(parameters.getFlowControlOutString());
		databitsChoice.setSelectedItem(parameters.getDatabitsString());
		stopbitsChoice.setSelectedItem(parameters.getStopbitsString());
		parityChoice.setSelectedItem(parameters.getParityString());
		
		super.propertiesToControls();
	}
}

