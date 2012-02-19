/*
 *		don@tourgeek.com
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.terminal.view;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

import org.jbundle.util.apprunner.*;
import org.jbundle.terminal.*;
import org.jbundle.terminal.control.*;
import org.jbundle.terminal.control.serial.*;
import org.jbundle.terminal.model.*;
import org.jbundle.terminal.view.*;

/**
 * ScreenView - The physical display screen.
 */
public class ScreenView extends JPanel
	implements BaseView
{
	protected BaseControl m_screenControl = null;
	
	/**
	 * The key listener that goes with this control.
	 */
	protected ScreenKeyHandler m_keyListener = null;

	/**
	 * Constructor.
	 */
	public ScreenView()
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public ScreenView(BaseControl screenControl, Properties properties)
	{
		this();
		this.init(screenControl, properties);
	}
	/**
	 * Constructor.
	 */
	public void init(BaseControl screenControl, Properties properties)
	{
		this.setScreenControl(screenControl);
		this.setProperties(properties);		// Get the properties and set up this control
	}
	/**
	 * Free the resources.
	 */
	 public void free()
	 {
		if (m_keyListener != null)
			this.removeKeyListener(m_keyListener);
		if (m_screenControl != null)
			if (m_screenControl.getScreenModel() != null)
				if (m_screenControl.getScreenModel().getScreenView() == this)
					m_screenControl.getScreenModel().setScreenView(null);
		m_screenControl = null;
	}
	/**
	 * Repaint the screen for this starting and ending locations.
	 * <p>Note: There may be small problem with syncronization if two timers
	 * call this at the same time from different tasks (since I use the
	 * shared m_rectTemp), but the resulting
	 * outcome should only cause a quick visual hickup which will be corrected
	 * on the next timer click.
	 */
	public void repaintChars(Point ptStartChange, Point ptEndChange)
	{
		// Add code in overridden class.
	}
	/**
	 * Get the control.
	 */
	public BaseControl getScreenControl()
	{
		return m_screenControl;
	}
	/**
	 * Set the control.
	 */
	public void setScreenControl(BaseControl screenControl)
	{
		if (m_keyListener != null)
			this.removeKeyListener(m_keyListener);
		m_screenControl = screenControl;
		if (m_screenControl != null)
		{
			m_keyListener = this.createKeyListener(m_screenControl);
			this.addKeyListener(m_keyListener);	// Keyboard events go to the terminal
		}
	}
	/**
	 * Create a listener for this control.
	 */
	public ScreenKeyHandler createKeyListener(BaseControl screenControl)
	{
		return screenControl.getScreenModel().createKeyListener(screenControl);
	}
	/**
	 * Get the key listener for this control.
	 */
	public ScreenKeyHandler getKeyListener()
	{
	    return m_keyListener;
	}
	/**
	 * Reset this control up to implement these new properties.
	 */
	public void setProperties(Properties properties)
	{
		// Override this!
	}
	/**
	 * Screen that is used to change the properties.
	 */
	public PropertyView getPropertyView(Properties properties)
	{
		return new PropertyView(this, properties);
	}
}
