/*
 * ModelData.java
 *
 * Created on April 19, 2000, 5:05 AM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.terminal.server;

import java.io.*;
import java.awt.*;
/** 
 * This is just a simple data holder (used to send data over the pipe).
 * @author  Administrator
 * @version 
 */
public class ModelData
	implements Serializable
{
	/**
	 * Physical screen data.
	 */
	protected char[][] m_chScreenMatrix = null;
	/**
	 * Attribute for each cell.
	 */
	protected short[][] m_rgsAttribute = null;
  
	public ModelData(char[][] chScreenMatrix, short[][] rgsAttribute)
	{
		m_chScreenMatrix = chScreenMatrix;
		m_rgsAttribute = rgsAttribute;
	}
	/**
	 * Physical screen data.
	 */
	public char[][] getCharacters()
	{
		return m_chScreenMatrix;
	}
	/**
	 * Attribute for each cell.
	 */
	
	public short[][] getAttributes()
	{
		return m_rgsAttribute;
	}
}
