/*
 * @(#)ScreenApplet.java	1.13 98/08/28
 * Created on April 19, 2000, 3:11 AM
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.terminal;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jbundle.util.apprunner.PropertyOwner;
import org.jbundle.util.apprunner.PropertyView;
/**
 * Main property view screen.
 * From this screen the user selects the model, view, and control.
 */
public class MainPropertyView extends PropertyView
	implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	public static final String MODEL_PARAM = "model";
	public static final String VIEW_PARAM = "view";
	public static final String CONTROL_PARAM = "control";
	protected static final String[] m_rgstrModels = {
		"DataGeneral"};
	protected static final String[] m_rgstrViews = {
		"Monitor",
		"Screen"};
	protected static final String[] m_rgstrControls = {
		"Serial",
		"Rmi",
		"Loopback"};
	protected JComboBox m_comboModel = null;
	protected JComboBox m_comboView = null;
	protected JComboBox m_comboControl = null;
	protected JButton m_buttonModel = null;
	protected JButton m_buttonView = null;
	protected JButton m_buttonControl = null;

	/**
	 * Constructor.
	 */
	public MainPropertyView()
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public MainPropertyView(PropertyOwner propOwner, Properties properties)
	{
		this();
		this.init(propOwner, properties);
	}
	/**
	 * Initialize.
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
		
		panelMain.setLayout(new GridLayout(3, 3));

		String strModel = m_properties.getProperty(MODEL_PARAM);
		if (strModel == null)
			strModel = m_rgstrModels[0];
		panelMain.add(new JLabel("Model:", JLabel.LEFT));
		panelMain.add(m_comboModel = (JComboBox)this.makeControlPopup(m_rgstrModels, strModel));
		panelMain.add(m_buttonModel = (JButton)new JButton("Change settings..."));
		m_buttonModel.addActionListener(this);

		String strView = m_properties.getProperty(VIEW_PARAM);
		if (strView == null)
			strView = m_rgstrViews[0];
		panelMain.add(new JLabel("View:", JLabel.LEFT));
		panelMain.add(m_comboView = (JComboBox)this.makeControlPopup(m_rgstrViews, strView));
		panelMain.add(m_buttonView = (JButton)new JButton("Change settings..."));
		m_buttonView.addActionListener(this);

		String strControl = m_properties.getProperty(CONTROL_PARAM);
		if (strControl == null)
			strControl = m_rgstrControls[0];
		panelMain.add(new JLabel("Control:", JLabel.LEFT));
		panelMain.add(m_comboControl = (JComboBox)this.makeControlPopup(m_rgstrControls, strControl));
		panelMain.add(m_buttonControl = (JButton)new JButton("Change settings..."));
		m_buttonControl.addActionListener(this);
		
		JPanel panelSub = this.makeNewPanel(panel, BorderLayout.SOUTH);
		super.addControlsToView(panelSub);
	}
	/**
	 * Set the properties to the current control values.
	 */
	public void controlsToProperties()
	{
		String strSelection = (String)m_comboModel.getSelectedItem();
		String strCurrent = m_properties.getProperty(MODEL_PARAM);
		BaseControl screenControl = ((BaseView)m_propOwner).getScreenControl();
		BaseModel screenModel = screenControl.getScreenModel();
		if (!strSelection.equals(strCurrent))
		{
			m_properties.setProperty(MODEL_PARAM, strSelection);
			if (screenModel != null)
				screenModel.free();
			screenModel = Utility.createModelFromProperties((BaseView)m_propOwner, m_properties);
		}
		strSelection = (String)m_comboControl.getSelectedItem();
		strCurrent = m_properties.getProperty(CONTROL_PARAM);
		if (!strSelection.equals(strCurrent))
		{
			m_properties.setProperty(CONTROL_PARAM, strSelection);
			if (screenControl != null)
				screenControl.free();
			screenControl = Utility.createControlFromProperties(screenModel, m_properties);
		}
		strSelection = (String)m_comboView.getSelectedItem();
		strCurrent = m_properties.getProperty(VIEW_PARAM);
		if (!strSelection.equals(strCurrent))
		{
			m_properties.setProperty(VIEW_PARAM, strSelection);
			if (m_propOwner != null)
				((BaseView)m_propOwner).free();
			m_propOwner = Utility.createViewFromProperties(screenControl, m_properties);
		}
		// Now, make sure the links are correct
		((BaseView)m_propOwner).setScreenControl(screenControl);
		screenControl.setScreenModel(screenModel);
		screenModel.setScreenView((BaseView)m_propOwner);
		super.controlsToProperties();
	}
	/**
	 * Set the properties to the current control values.
	 */
	public void propertiesToControls()
	{
		m_comboModel.setSelectedItem(m_properties.getProperty(MODEL_PARAM));
		m_comboControl.setSelectedItem(m_properties.getProperty(CONTROL_PARAM));
		m_comboView.setSelectedItem(m_properties.getProperty(VIEW_PARAM));
		super.propertiesToControls();
	}
	/**
	 * User pressed a button.
	 * @param e The action event.
	 */
	public void actionPerformed(ActionEvent e)
	{
		PropertyOwner propOwner = null;
		if (e.getSource() == m_buttonModel)
			propOwner = ((BaseView)m_propOwner).getScreenControl().getScreenModel();
		else if (e.getSource() == m_buttonControl)
			propOwner = ((BaseView)m_propOwner).getScreenControl();
		else if (e.getSource() == m_buttonView)
			propOwner = m_propOwner;
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
	}
	/**
	 * Get the description of this option panel.
	 * @return The description of this view (override in other views).
	 */
	public String getDescription()
	{
		return "Main properties";
	}
}
