/*
 * ArcPanel.java
 *
 * Created on January 14, 2001, 12:39 AM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package com.tourstudio.monitor.awt;

import java.lang.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.text.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Arc2D;
import java.awt.geom.AffineTransform;

/** 
 * ArcPanel - This is simple panel which always tries to make the passed in panel square.
 * @author  Don Corley
 * @version 
 */
public class CenteredPanel extends JPanel
{
	/*
	 * Creates new Model.
	 */
	public CenteredPanel(JComponent panel)
	{
		super();
		this.setOpaque(false);		// By default
		this.setLayout(new BorderLayout());
		
		this.add(BorderLayout.CENTER, panel);
		this.add(BorderLayout.NORTH, new XPanel());
		this.add(BorderLayout.SOUTH, new XPanel());
		this.add(BorderLayout.EAST, new XPanel());
		this.add(BorderLayout.WEST, new XPanel());
	}
	class XPanel extends JPanel
	{
		public XPanel()
		{
			super();
			this.setOpaque(false);
		}
		/*
		 * Make the surrounding panels so the center panel will be square.
		 */
		public Dimension getPreferredSize()
		{
			Dimension dim = super.getPreferredSize();
			Container parent = this.getParent();
			if (parent != null)
			{
				Rectangle dimParent = parent.getBounds();
				int iMin = Math.min(dimParent.width, dimParent.height);
				dim.width = (dimParent.width - iMin) / 2;
				dim.height = (dimParent.height - iMin) / 2;
			}
			return dim;
		}
	}
}
