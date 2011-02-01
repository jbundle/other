/*
 * Copyright (c) 2000 jbundle.org. All Rights Reserved.
 */
package org.jbundle.terminal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
/**
 * Utilities.
 */
public class Utility extends Object
{
	/**
	 * Set up the class name. com.tourstudio.terminal.suffix.classname.ClassNameClassSuffix.
	 * @param strName The name of the class.
	 * @param strClassSuffix The classname suffix.
	 * @return The full class name.
	 */
	public static String setupClassName(String strName, String strClassSuffix)
	{
		if (strName == null)
			strName = Utility.getDefaultClassName(strClassSuffix);
		if (strName.equals("Screen"))
			return "com.tourstudio.terminal." + strClassSuffix.toLowerCase() + '.' + "Screen" + strClassSuffix;
		if (strName.indexOf('.') != -1)
			return strName;
		return "com.tourstudio.terminal." + strClassSuffix.toLowerCase() + '.' + strName.toLowerCase() + '.' + strName + strClassSuffix;
	}
	/**
	 * Get the default class name for this type of object.
	 * @param strClassSuffix The type of class (Model/View or Control)
	 * @return The default class name.
	 */
	public static String getDefaultClassName(String strClassSuffix)
	{
		String strName = "Screen";
		if (strClassSuffix.equalsIgnoreCase(MainPropertyView.MODEL_PARAM))
			strName = "DataGeneral";
		if (strClassSuffix.equalsIgnoreCase(MainPropertyView.VIEW_PARAM))
			strName = "Monitor";
		if (strClassSuffix.equalsIgnoreCase(MainPropertyView.CONTROL_PARAM))
			strName = "Serial";
		return strName;
	}
	/**
	 * Create this an object from this class name.
	 * @param strClassName Class name
	 * @return The object (null if not found)
	 */
	public static Object makeObjectFromClassName(String strClassName)
	{
		Object panel = null;
		try	{
			if (strClassName.indexOf('.') == 0)
				strClassName = org.jbundle.terminal.TerminalConstants.ROOT_PACKAGE + strClassName.substring(1);
			Class<?> c = Class.forName(strClassName);
			if (c != null)
			{
				panel = c.newInstance();
			}
		} catch (Exception ex)	{
			ex.printStackTrace();
			panel = null;
		}
		return panel;
	}
	/**
	 * Create a new model from these properties.
	 * @param screenView The view to add this model to.
	 * @param properties The properties for this model are in here.
	 * @return The new model.
	 */
	public static BaseModel createModelFromProperties(BaseView screenView, Properties properties)
	{
		String strClassName = Utility.setupClassName(properties.getProperty(MainPropertyView.MODEL_PARAM), "Model");
		BaseModel screenModel = (BaseModel)Utility.makeObjectFromClassName(strClassName);
		if (screenModel != null)
			screenModel.init(screenView, properties);
		if (screenModel == null)	// Default
			screenModel = new org.jbundle.terminal.model.datageneral.DataGeneralModel(screenView, properties);	// Standard 24 x 80 Screen
		return screenModel;
	}
	/**
	 * Create a new control for this view.
	 * @param screenModel The model to add this control to.
	 * @param properties The properties for this control are in here.
	 * @return The new control.
	 */
	public static BaseControl createControlFromProperties(BaseModel screenModel, Properties properties)
	{
		String strClassName = Utility.setupClassName(properties.getProperty(MainPropertyView.CONTROL_PARAM), "Control");
		BaseControl screenControl = (BaseControl)Utility.makeObjectFromClassName(strClassName);
		if (screenControl != null)
			screenControl.init(screenModel, properties);
		if (screenControl == null)	// Default
			screenControl = new org.jbundle.terminal.control.loopback.LoopbackControl(screenModel, properties);
		return screenControl;
	}
    /**
     * Create an view for this control.  
	 * @param screenModel The control to add this control to.
	 * @param properties The properties for this view are in here.
	 * @return The new view.
     */
    public static BaseView createViewFromProperties(BaseControl screenControl, Properties properties)
	{
		String strClassName = Utility.setupClassName(properties.getProperty(MainPropertyView.VIEW_PARAM), "View");
		BaseView screenView = (BaseView)Utility.makeObjectFromClassName(strClassName);
		if (screenView != null)
			screenView.init(screenControl, properties);
		if (screenView == null)	// Default
			screenView = new org.jbundle.terminal.view.monitor.MonitorView(screenControl, properties);
		return screenView;
    }
	/**
	 * Get the properties.
	 * @param applet If this is an applet.
	 * @properties The default properties.
	 * @return The new properties.
	 */
	public static Properties getProperties(Main applet, Properties properties)
	{
		if ((applet == null)
			|| (Main.grgArgs != null))
		{		// Standalone app
			File fileProperties = Utility.getPropertiesFile(properties);
			if (fileProperties == null)
			{
				try {
					String strFileName = properties.getProperty(Main.PARAM_PROPERTY_FILE);
					URL url = new URL(strFileName);
					InputStream inStream = url.openStream();
					properties.load(inStream);
					inStream.close();
				} catch(MalformedURLException ex) {
					ex.printStackTrace();
				} catch (IOException ex)	{
					ex.printStackTrace();
				}
				return properties;
			}
			try	{
				InputStream inStream = new FileInputStream(fileProperties);
				properties.load(inStream);
				inStream.close();
			} catch (IOException ex)	{
				ex.printStackTrace();
			}
		}
		else
		{
			String strFileName = properties.getProperty(Main.PARAM_PROPERTY_FILE);
			if ((strFileName != null)
				&& (strFileName.length() > 0))
			{
				try {
					URL url = new URL(applet.getCodeBase(), strFileName);
					InputStream inStream = url.openStream();
					properties.load(inStream);
					inStream.close();
				} catch(MalformedURLException ex) {
					ex.printStackTrace();
					return properties;
				} catch (IOException ex)	{
					ex.printStackTrace();
					return properties;
				}
			}
		}
		return properties;
	}
	/**
	 * Write the properties file.
	 * @param properties The properties to write.
	 */
	public static void writeProperties(Properties properties)
	{
		File fileProperties = Utility.getPropertiesFile(properties);
		try	{
			OutputStream out = new FileOutputStream(fileProperties);
			properties.store(out, "Terminal preferences");
			out.flush();
			out.close();
		} catch (IOException ex)	{
			ex.printStackTrace();
		}
	}
	/**
	 * Get the property file.
	 * @param properties The properties to write.
	 * @return File The properties file.
	 */
	public static File getPropertiesFile(Properties properties)
	{
		String strFileName = properties.getProperty(Main.PARAM_PROPERTY_FILE, "./etc/Terminal.properties");
		Properties propSystem = null;
		try	{
			propSystem = System.getProperties();
		} catch (Exception ex)	{
			return null;	// In a sandbox - be careful 
		}
		String strUserDir = propSystem.getProperty("user.dir");
		String strSeparator = propSystem.getProperty("file.separator");
		char chSeparator = '/';
		if (strSeparator != null)
			if (strSeparator.length() == 1)
				chSeparator = strSeparator.charAt(0);
		if (strUserDir != null)
			strFileName = strUserDir + strSeparator + strFileName;
		File fileProperties = new File(strFileName);
		try	{
			if (!fileProperties.exists())
			{
				int iSeparator = strFileName.lastIndexOf('/');
				if (iSeparator == -1)
					iSeparator = strFileName.lastIndexOf(chSeparator);
				if (iSeparator != -1)
					new File(strFileName.substring(0, iSeparator)).mkdirs();
				fileProperties.createNewFile();
			}
		} catch (IOException ex)	{
			ex.printStackTrace();
		}
		return fileProperties;
	}
}
