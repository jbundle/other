/**
 * Copyright (c) 2000 jbundle.org. All Rights Reserved.
 *		don@tourgeek.com
 */
package org.jbundle.terminal;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

import org.jbundle.jproperties.*;

/**
 * BaseControl - Represents the computer ScreenControl.
 * The computer ScreenControl receives
 * data from the host, then sends it to the model.
 * Optionally, a 'serialOut' object is supplied in the cases
 * where the control needs to send data back to the source
 * (For example: get current cursor position sends data back).
 */
public interface BaseControl extends PropertyOwner
{
	/**
	 * Constructor.
	 */
	public void init(BaseModel screenModel, Properties properties);
	/**
	 * Free control.
	 */
	public void free();
	/**
	 * Get screen model.
	 */
	public BaseModel getScreenModel();
	/**
	 * Get screen model.
	 */
	public void setScreenModel(BaseModel screenModel);
	/**
	 * Send this character to the control.
	 */
	public void sendCharToControl(char chChar);
}
