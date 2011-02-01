/* @(#)SerialPropertyView.java	1.5 98/07/17 SMI
 * Copyright (c) 2000 jbundle.org. All Rights Reserved.
 *		don@tourgeek.com
 */
package org.jbundle.terminal.model;

import javax.comm.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import org.jbundle.jproperties.*;
import org.jbundle.terminal.*;
/**
 * GUI element that holds the user changable elements for connection
 * configuration.
 */
public class ScreenPropertyView extends PropertyView
	implements ItemListener
{
	private JLabel m_cursorBlinkLabel;
	private JCheckBox m_cursorBlinkChoice;

	private JLabel m_charBlinkLabel;
	private JCheckBox m_charBlinkChoice;

	protected ScreenModel m_screenModel = null;

	public static final String CURSOR_BLINK_PARAM = "screen.cursorblink";
	public static final String CHAR_BLINK_PARAM = "screen.charblink";

	/**
	 * Creates and initilizes the configuration panel. The initial settings
	 * are from the parameters object.
	 */
	public ScreenPropertyView()
	{
		super();
	}
	/**
	 * Creates and initilizes the configuration panel. The initial settings
	 * are from the parameters object.
	 */
	public ScreenPropertyView(PropertyOwner propOwner, Properties properties)
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

		m_screenModel = (ScreenModel)this.getPropertyOwner();

		panelMain.setLayout(new GridLayout(2, 2));
	
		m_cursorBlinkLabel = new JLabel("Blink cursor: ", Label.LEFT);
		panelMain.add(m_cursorBlinkLabel);
		m_cursorBlinkChoice = new JCheckBox();
		panelMain.add(m_cursorBlinkChoice);

		m_charBlinkLabel = new JLabel("Blink characters: ", Label.LEFT);
		panelMain.add(m_charBlinkLabel);
		m_charBlinkChoice = new JCheckBox();
		panelMain.add(m_charBlinkChoice);

		JPanel panelSub = this.makeNewPanel(panel, BorderLayout.SOUTH);
		super.addControlsToView(panelSub);
	}
	/**
	 * Add any listeners.
	 */
	public void addListeners()
	{
		m_cursorBlinkChoice.addItemListener(this);
		m_charBlinkChoice.addItemListener(this);
		super.addListeners();
	}
	/**
	Event handler for changes in the current selection of the Choices.
	If a port is open the port can not be changed.
	If the choice is unsupported on the platform then the user will
	be notified and the settings will revert to their pre-selection
	state.
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
		return "Screen control properties";
	}
	/**
	 * Set the properties to the current control values.
	 */
	public void controlsToProperties()
	{	// Override this
		boolean bIsSelected = m_cursorBlinkChoice.isSelected();
		String strState = "false";
		if (bIsSelected)
			strState = "true";
		m_properties.setProperty(CURSOR_BLINK_PARAM, strState);			// Make sure all the properties are set.

		bIsSelected = m_charBlinkChoice.isSelected();
		strState = "false";
		if (bIsSelected)
			strState = "true";
		m_properties.setProperty(CHAR_BLINK_PARAM, strState);			// Make sure all the properties are set.

		m_screenModel.setProperties(m_properties);
		
		super.controlsToProperties();
	}
	/**
	 * Set the controls to the current property values.
	 */
	public void propertiesToControls()
	{	// Override this
		String strPrint = m_properties.getProperty(CURSOR_BLINK_PARAM);
		boolean bIsSelected = true;
		if ((strPrint != null) && (strPrint.length() > 0))
			if ((strPrint.charAt(0) == 'f')
			|| (strPrint.charAt(0) == 'F')
			|| (strPrint.charAt(0) == 'n')
			|| (strPrint.charAt(0) == 'N'))
				bIsSelected = false;
		m_cursorBlinkChoice.setSelected(bIsSelected);

		strPrint = m_properties.getProperty(CHAR_BLINK_PARAM);
		bIsSelected = true;
		if ((strPrint != null) && (strPrint.length() > 0))
			if ((strPrint.charAt(0) == 'f')
			|| (strPrint.charAt(0) == 'F')
			|| (strPrint.charAt(0) == 'n')
			|| (strPrint.charAt(0) == 'N'))
				bIsSelected = false;
		m_charBlinkChoice.setSelected(bIsSelected);
		
		super.propertiesToControls();
	}
}

