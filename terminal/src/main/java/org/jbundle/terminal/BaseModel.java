/*
 *		Don_Corley@msn.com
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.terminal;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

import org.jbundle.util.apprunner.*;

/**
 * The interface for Models.
 */
public interface BaseModel extends PropertyOwner
{
	/**
	 * Set up the model.
	 */
	public void init(BaseView screenView, Properties properties);
	/**
	 * Free model.
	 */
	public void free();
	/**
	 * Get screen model.
	 */
	public BaseView getScreenView();
	/**
	 * Get screen model.
	 */
	public void setScreenView(BaseView screenView);
	/**
	 * Send this character to the control.
	 */
	public void sendChar(char chChar);
	/**
	 * Create a listener for this control.
	 */
	public org.jbundle.terminal.model.ScreenKeyHandler createKeyListener(BaseControl screenControl);
}
