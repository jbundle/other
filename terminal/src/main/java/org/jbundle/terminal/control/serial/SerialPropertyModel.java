/*
 *
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package org.jbundle.terminal.control.serial;

import javax.comm.*;
import java.util.*;

/**
 * A class that stores parameters for serial ports. 
*/
public class SerialPropertyModel {

    private String m_portName;
    private int m_baudRate;
    private int m_flowControlIn;
    private int m_flowControlOut;
    private int m_databits;
    private int m_stopbits;
    private int m_parity;

    /**
     * Default constructer. Sets parameters to no port, 9600 baud, no flow 
     * control, 8 data bits, 1 stop bit, no parity.
     */
    public SerialPropertyModel () {
	}
    /**
     * Paramaterized constructer.

     * @param portName The name of the port.
     * @param baudRate The baud rate.
     * @param flowControlIn Type of flow control for receiving.
     * @param flowControlOut Type of flow control for sending.
     * @param databits The number of data bits.
     * @param stopbits The number of stop bits.
     * @param parity The type of parity.
     */
    public SerialPropertyModel(Properties properties)
	{
		String strPortName = null; 
		String strBaudRate = null;
		String strFlowControlIn = null;
		String strFlowControlOut = null;
		String strDatabits = null;
		String strStopbits = null;
		String strParity = null;
		if (properties != null)
		{
			strPortName = properties.getProperty(SerialPropertyView.PORT_PARAM); 
			strBaudRate = properties.getProperty(SerialPropertyView.BAUD_PARAM);
			strFlowControlIn = properties.getProperty(SerialPropertyView.FLOWCONTROLIN_PARAM);
			strFlowControlOut = properties.getProperty(SerialPropertyView.FLOWCONTROLOUT_PARAM);
			strDatabits = properties.getProperty(SerialPropertyView.DATABITS_PARAM);
			strStopbits = properties.getProperty(SerialPropertyView.STOPBITS_PARAM);
			strParity = properties.getProperty(SerialPropertyView.PARITY_PARAM);
		}
    	this.init(strPortName, strBaudRate, strFlowControlIn, strFlowControlOut,
			strDatabits, strStopbits, strParity);
    }
	/**
	 * Initialize.
	 */
    public void init(String strPortName, String strBaudRate, String strFlowControlIn,
		String strFlowControlOut, String strDatabits, String strStopbits, String strParity)
	{
		if (strPortName == null)
			m_portName = "COM1";
		else
			this.setPortName(strPortName);
		if (strBaudRate == null)
			m_baudRate = 9600;
		else
			this.setBaudRate(strBaudRate);
		if (strFlowControlIn == null)
			m_flowControlIn = SerialPort.FLOWCONTROL_NONE;
		else
			this.setFlowControlIn(strFlowControlIn);
		if (strFlowControlOut == null)
			m_flowControlOut = SerialPort.FLOWCONTROL_NONE;
		else
			this.setFlowControlOut(strFlowControlOut);
		if (strDatabits == null)
			m_databits = SerialPort.DATABITS_8;
		else
			this.setDatabits(strDatabits);
		if (strStopbits == null)
			m_stopbits = SerialPort.STOPBITS_1;
		else
			this.setStopbits(strStopbits);
		if (strParity == null)
			m_parity = SerialPort.PARITY_NONE;
		else
			this.setParity(strParity);
    }		
	/**
	 * Move the current params to this property object.
	 */
    public void getProperties(Properties properties)
	{
		String strPortName = this.getPortName();
		String strBaudRate = this.getBaudRateString();
		String strFlowControlIn = this.getFlowControlInString();
		String strFlowControlOut = this.getFlowControlOutString();
		String strDatabits = this.getDatabitsString();
		String strStopbits = this.getStopbitsString();
		String strParity = this.getParityString();
		properties.setProperty(SerialPropertyView.PORT_PARAM, strPortName); 
		properties.setProperty(SerialPropertyView.BAUD_PARAM, strBaudRate);
		properties.setProperty(SerialPropertyView.FLOWCONTROLIN_PARAM, strFlowControlIn);
		properties.setProperty(SerialPropertyView.FLOWCONTROLOUT_PARAM, strFlowControlOut);
		properties.setProperty(SerialPropertyView.DATABITS_PARAM, strDatabits);
		properties.setProperty(SerialPropertyView.STOPBITS_PARAM, strStopbits);
		properties.setProperty(SerialPropertyView.PARITY_PARAM, strParity);
    }		
    /**
     * Sets port name.
     * @param portName New port name.
     */
    public void setPortName(String portName) {
		m_portName = portName;
    }
    /**
     * Gets port name.
     * @return Current port name.
     */
    public String getPortName() {
		return m_portName;
    }
    /**
     * Sets baud rate.
     * @param baudRate New baud rate.
     */
    public void setBaudRate(int baudRate) {
		m_baudRate = baudRate;
    }
    /**
     * Sets baud rate.
     * @param baudRate New baud rate.
     */
    public void setBaudRate(String baudRate) {
	m_baudRate = Integer.parseInt(baudRate);
    }
    /**
     * Gets baud rate as an <code>int</code>.
     * @return Current baud rate.
     */
    public int getBaudRate() {
		return m_baudRate;
    }
    /**
     * Gets baud rate as a <code>String</code>.
     * @return Current baud rate.
     */
    public String getBaudRateString() {
		return Integer.toString(m_baudRate);
    }
    /**
     * Sets flow control for reading.
     * @param flowControlIn New flow control for reading type.
     */
    public void setFlowControlIn(int flowControlIn) {
		m_flowControlIn = flowControlIn;
    }
    /**
     * Sets flow control for reading.
     * @param flowControlIn New flow control for reading type.
     */
    public void setFlowControlIn(String flowControlIn) {
		m_flowControlIn = stringToFlow(flowControlIn);
    }
    /** 
     * Gets flow control for reading as an <code>int</code>.
     * @return Current flow control type.
     */
    public int getFlowControlIn() {
		return m_flowControlIn;
    }
    /** 
     * Gets flow control for reading as a <code>String</code>.
     * @return Current flow control type.
     */
    public String getFlowControlInString() {
		return flowToString(m_flowControlIn);
    }
    /**
     * Sets flow control for writing.
     * @param flowControlIn New flow control for writing type.
     */
    public void setFlowControlOut(int flowControlOut) {
		m_flowControlOut = flowControlOut;
    }
    /**
     * Sets flow control for writing.
     * @param flowControlIn New flow control for writing type.
     */
    public void setFlowControlOut(String flowControlOut) {
		m_flowControlOut = stringToFlow(flowControlOut);
    }
    /** 
     * Gets flow control for writing as an <code>int</code>.
     * @return Current flow control type.
    */
    public int getFlowControlOut() {
		return m_flowControlOut;
    }
    /** 
     * Gets flow control for writing as a <code>String</code>.
     * @return Current flow control type.
     */
    public String getFlowControlOutString() {
		return flowToString(m_flowControlOut);
    }
    /** 
     * Sets data bits.
     * @param databits New data bits setting.
     */
    public void setDatabits(int databits) {
		m_databits = databits;
    }
    /** 
     * Sets data bits.
     * @param databits New data bits setting.
     */
    public void setDatabits(String databits) {
		if (databits.equals("5")) {
			m_databits = SerialPort.DATABITS_5;
		}
		if (databits.equals("6")) {
			m_databits = SerialPort.DATABITS_6;
		}
		if (databits.equals("7")) {
			m_databits = SerialPort.DATABITS_7;
		}
		if (databits.equals("8")) {
			m_databits = SerialPort.DATABITS_8;
		}
    }
    /**
     * Gets data bits as an <code>int</code>.
     * @return Current data bits setting.
     */
    public int getDatabits() {
		return m_databits;
    }
    /**
     * Gets data bits as a <code>String</code>.
     * @return Current data bits setting.
     */
    public String getDatabitsString() {
		switch(m_databits) {
			case SerialPort.DATABITS_5:
			return "5";
			case SerialPort.DATABITS_6:
			return "6";
			case SerialPort.DATABITS_7:
			return "7";
			case SerialPort.DATABITS_8:
			return "8";
			default:
			return "8";
		}
    }
    /**
     * Sets stop bits.
     * @param stopbits New stop bits setting.
     */
    public void setStopbits(int stopbits) {
		m_stopbits = stopbits;
    }
    /**
     * Sets stop bits.
     * @param stopbits New stop bits setting.
     */
    public void setStopbits(String stopbits) {
		if (stopbits.equals("1")) {
			m_stopbits = SerialPort.STOPBITS_1;
		}
		if (stopbits.equals("1.5")) {
			m_stopbits = SerialPort.STOPBITS_1_5;
		}
		if (stopbits.equals("2")) {
			m_stopbits = SerialPort.STOPBITS_2;
		}
    }
    /**
     * Gets stop bits setting as an <code>int</code>.
     * @return Current stop bits setting.
     */
    public int getStopbits() {
		return m_stopbits;
    }
    /**
     * Gets stop bits setting as a <code>String</code>.
     * @return Current stop bits setting.
     */
    public String getStopbitsString() {
		switch(m_stopbits) {
			case SerialPort.STOPBITS_1:
			return "1";
			case SerialPort.STOPBITS_1_5:
			return "1.5";
			case SerialPort.STOPBITS_2:
			return "2";
			default:
			return "1";
		}
    }
    /**
     * Sets parity setting.
     * @param parity New parity setting.
     */
    public void setParity(int parity) {
		m_parity = parity;
    }
    /**
     * Sets parity setting.
     * @param parity New parity setting.
     */
    public void setParity(String parity) {
		if (parity.equals("None")) {
			m_parity = SerialPort.PARITY_NONE;
		}
		if (parity.equals("Even")) {
			m_parity = SerialPort.PARITY_EVEN;
		}
		if (parity.equals("Odd")) {
			m_parity = SerialPort.PARITY_ODD;
		}
    }
    /**
     * Gets parity setting as an <code>int</code>.
     * @return Current parity setting.
     */
    public int getParity() {
		return m_parity;
    }
    /**
     * Gets parity setting as a <code>String</code>.
     * @return Current parity setting.
     */
    public String getParityString() {
		switch(m_parity) {
			case SerialPort.PARITY_NONE:
			return "None";
			case SerialPort.PARITY_EVEN:
			return "Even";
			case SerialPort.PARITY_ODD:
			return "Odd";
			default:
			return "None";
		}
    }
    /**
     * Converts a <code>String</code> describing a flow control type to an
     * <code>int</code> type defined in <code>SerialPort</code>.
     * @param flowControl A <code>string</code> describing a flow control type.
     * @return An <code>int</code> describing a flow control type.
    */
    private int stringToFlow(String flowControl) {
		if (flowControl.equals("None")) {
			return SerialPort.FLOWCONTROL_NONE;
		}
		if (flowControl.equals("Xon/Xoff Out")) {
			return SerialPort.FLOWCONTROL_XONXOFF_OUT;
		}
		if (flowControl.equals("Xon/Xoff In")) {
			return SerialPort.FLOWCONTROL_XONXOFF_IN;
		}
		if (flowControl.equals("RTS/CTS In")) {
			return SerialPort.FLOWCONTROL_RTSCTS_IN;
		}
		if (flowControl.equals("RTS/CTS Out")) {
			return SerialPort.FLOWCONTROL_RTSCTS_OUT;
		}
		return SerialPort.FLOWCONTROL_NONE;
    }
    /**
     * Converts an <code>int</code> describing a flow control type to a 
     * <code>String</code> describing a flow control type.
     * @param flowControl An <code>int</code> describing a flow control type.
     * @return A <code>String</code> describing a flow control type.
     */
    String flowToString(int flowControl) {
	switch(flowControl) {
	    case SerialPort.FLOWCONTROL_NONE:
		return "None";
	    case SerialPort.FLOWCONTROL_XONXOFF_OUT:
		return "Xon/Xoff Out";
	    case SerialPort.FLOWCONTROL_XONXOFF_IN:
		return "Xon/Xoff In";
	    case SerialPort.FLOWCONTROL_RTSCTS_IN:
		return "RTS/CTS In";
	    case SerialPort.FLOWCONTROL_RTSCTS_OUT:
		return "RTS/CTS Out";
	    default:
		return "None";
	}
    }
}
