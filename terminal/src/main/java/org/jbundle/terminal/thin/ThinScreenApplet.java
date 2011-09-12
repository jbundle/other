/*
 *		don@tourgeek.com
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.terminal.thin;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

import java.text.*;
import javax.swing.*;
import javax.swing.border.*;

import org.jbundle.terminal.*;
import org.jbundle.terminal.view.*;

/*
 * This is a class I use in my programs.
 * NOTE: It has been commented out to keep you from getting compile errors.
 */
public class ThinScreenApplet extends Object
{
	public ThinScreenApplet()	{super();}
}
/*
import com.tourstudio.thin.calendar.*;
import com.tourstudio.thin.screen.*;

public class ThinScreenApplet extends BaseApplet
{

	/**
	 *	OrderEntry Class Constructor.
	 */
/*	public ThinScreenApplet()
	{
		super();
	}
	/**
	 *	OrderEntry Class Constructor.
	 */
/*	public ThinScreenApplet(String args[])
	{
		this();
		this.init(args);
	}
    /**
     * Initializes the applet.  You never need to call this directly; it is
     * called automatically by the system once the applet is created.
     */
/*    public void init()
	{
		super.init();
    }

    /**
     * Called to start the applet.  You never need to call this directly; it
     * is called when the applet's document is visited.
     */
/*    public void start()
	{
		super.start();
	}
    /**
     * Add any applet sub-panel(s) now.
     */
/*    public boolean addSubPanels(Container parent)
	{
		parent.setLayout(new BoxLayout(parent, BoxLayout.X_AXIS));
		JScrollPane scroller = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
		scroller.setPreferredSize(new Dimension(800, 400));
		scroller.setAlignmentX(LEFT_ALIGNMENT);
		scroller.setAlignmentY(TOP_ALIGNMENT);

		ScreenView panel = new ScreenView(null, null);
		
		scroller.setViewportView(panel);
		parent.add(scroller);
    }
    /**
     * Called to stop the applet.  This is called when the applet's document is
     * no longer on the screen.  It is guaranteed to be called before destroy()
     * is called.  You never need to call this method directly
     */
/*    public void stop()
	{
		super.stop();
    }

    /**
     * Cleans up whatever resources are being held.  If the applet is active
     * it is stopped.
     */
/*    public void destroy()
	{
		super.destroy();
    }

    /**
     * For Stand-alone.
     */
/*    public static void main(String[] args)
	{
		
		BaseApplet.main(args);
		BaseApplet applet = ThinScreenApplet.getSharedInstance();
		if (applet == null)
			applet = new ThinScreenApplet(args);
		new JBaseFrame("Calendar", applet, args);
    }
}
*/
