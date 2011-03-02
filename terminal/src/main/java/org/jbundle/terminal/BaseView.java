/**
 * Copyright (c) 2000 jbundle.org. All Rights Reserved.
 *		Don_Corley@msn.com
 */
package org.jbundle.terminal;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

import org.jbundle.util.apprunner.*;

/**
 * This is the interface for a View.
 */
public interface BaseView extends PropertyOwner
{
	/**
	 * Constructor.
	 */
	public void init(BaseControl screenControl, Properties properties);
	/**
	 * Free the resources.
	 */
	 public void free();
	/**
	 * Get the control.
	 * @return The control.
	 */
	public BaseControl getScreenControl();
	/**
	 * Set the control.
	 * @param screenControl The control.
	 */
	public void setScreenControl(BaseControl screenControl);
	/**
	 * Repaint the screen for this starting and ending locations.
	 */
	public void repaintChars(Point ptStartChange, Point ptEndChange);
}
