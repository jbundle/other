/**
 * Copyright (c) 2000 jbundle.org. All Rights Reserved.
 *		Don_Corley@msn.com
 */
package org.jbundle.terminal.control;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import org.jbundle.util.apprunner.*;
import org.jbundle.terminal.*;
import org.jbundle.terminal.control.*;
import org.jbundle.terminal.model.*;

/**
 * ScreenControl - Represents the computer ScreenControl.
 * The computer ScreenControl receives
 * data from the host, then sends it to the model.
 * Optionally, a 'serialOut' object is supplied in the cases
 * where the control needs to send data back to the source
 * (For example: get current cursor position sends data back).
 */
public class ScreenControl extends Object
	implements BaseControl
{
	/**
	 * Screen object to send characters to.
	 */
	protected BaseModel m_screenModel = null;

	/**
	 * Constructor.
	 */
	public ScreenControl()
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public ScreenControl(BaseModel screenModel, Properties properties)
	{
		this();
		this.init(screenModel, properties);
	}
	/**
	 * Constructor.
	 */
	public void init(BaseModel screenModel, Properties properties)
	{
		this.setScreenModel(screenModel);
		this.setProperties(properties);		// Get the properties and set up this control
	}
	/**
	 * Free this control.
	 */
	public void free()
	{
		if (m_screenModel != null)
			if (m_screenModel.getScreenView() != null)
				if (m_screenModel.getScreenView().getScreenControl() == this)
					m_screenModel.getScreenView().setScreenControl(null);
		m_screenModel = null;
	}
	/**
	 * Get screen.
	 */
	public BaseModel getScreenModel()
	{
		return m_screenModel;
	}
	/**
	 * Get screen.
	 */
	public void setScreenModel(BaseModel screenModel)
	{
		m_screenModel = screenModel;
	}
	/**
	 * Send this character to the control.
	 */
	public void sendCharToControl(char chChar)
	{	// Override this to send characters to the physical control.
	}
	/**
	 * Send this character to the model.
	 * ie., The character was received from the physical control... pass it on.
	 */
	public void sendCharToModel(char chChar)
	{	// Override this to send characters
		if (this.getScreenModel() != null)
			this.getScreenModel().sendChar(chChar);
	}
	/**
	 * Set this control up to implement these properties.
	 */
	public void setProperties(Properties properties)
	{	// Overriding classes may need to check properties for settings
	}
	/**
	 * Screen that is used to change the properties.
	 */
	public PropertyView getPropertyView(Properties properties)
	{
		return new PropertyView(this, properties);
	}
}
