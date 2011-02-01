/*
 * ArcPanel.java
 *
 * Created on January 14, 2001, 12:39 AM
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
 * ArcPanel - This is simple panel which displays a pie chart.
 * @author  Don Corley
 * @version 
 */
public class ArcPanel extends JPanel
{
	protected double m_dLoadPercent = 0.0;
	protected String m_strDescription = null;

	/*
	 * Creates new Model.
	 */
	public ArcPanel()
	{
		super();
	}
	/*
	 * Set a new load percentage.
	 */
	public ArcPanel(String strDescription, double dLoadPercent)
	{
		this();
		this.init(strDescription, dLoadPercent);
	}
	/*
	 * Set a new load percentage.
	 */
	public void init(String strDescription, double dLoadPercent)
	{
		m_strDescription = strDescription;
		m_dLoadPercent = dLoadPercent;
		
		this.setOpaque(false);		// By default
	}
	/*
	 * Set a new load percentage.
	 */
	public void setDescription(String strDescription)
	{
		m_strDescription = strDescription;
		this.repaint();
	}
	/*
	 * Set a new load percentage.
	 */
	public void setPercentage(double dLoadPercent)
	{
		m_dLoadPercent = dLoadPercent;
		this.repaint();
	}
	/*
	 * Paint this area of the panel.
	 */
	public void paint(Graphics g)
	{
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		Rectangle bounds = this.getBounds();
		double angleStart = 0;
		double angleExtent = - 360 * m_dLoadPercent;		// Convert to degrees

		// fill Ellipse2D.Double
		g2.setPaint(Color.pink);
		g2.fill(new Ellipse2D.Double(0, 0, bounds.width, bounds.height));

		Arc2D pieArc = new Arc2D.Float(Arc2D.PIE);
		pieArc.setFrame(0, 0, bounds.width, bounds.height);
		pieArc.setAngleStart(angleStart);
		pieArc.setAngleExtent(angleExtent);
		g2.setColor(Color.blue);
		g2.fill(pieArc);

		g2.setStroke(new BasicStroke(2.0f));
		g2.setPaint(Color.black);
		g2.draw(new Ellipse2D.Double(0, 0, bounds.width, bounds.height));

		String strPercent = Integer.toString((int)(m_dLoadPercent * 100 + 0.5)) + '%';
		if (m_strDescription != null)
			strPercent = m_strDescription + " " + strPercent;
		g2.drawString(strPercent, (int)(bounds.width*.5), (int)(bounds.height*.5));
	}
}