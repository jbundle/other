/*
 *
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.terminal.control.serial;

import javax.comm.*;
import java.io.*;
import java.util.*;
import java.awt.TextArea;
import java.awt.event.*;
import java.util.TooManyListenersException;

import org.jbundle.util.apprunner.*;
import org.jbundle.terminal.*;
import org.jbundle.terminal.control.*;
import org.jbundle.terminal.control.remote.*;

/**
A class that handles the details of a serial connection. Reads from one 
TextArea and writes to a second TextArea. 
Holds the state of the connection.
*/
public class SerialControl extends RemoteControl
	implements SerialPortEventListener,
				CommPortOwnershipListener
{
    public SerialPropertyModel m_parameters = null;
    private OutputStream os = null;
    private InputStream is = null;

    private CommPortIdentifier portId = null;
    private SerialPort sPort = null;

    private boolean m_bOpen = false;

	/**
	 * Constructor.
	 */
	public SerialControl()
	{
		super();
	}
	/**
	 * Constructor.
	 */
	public SerialControl(BaseModel screenModel, Properties properties)
	{
		this();
		this.init(screenModel, properties);
	}
	/**
	 * Constructor.
	 */
	public void init(BaseModel screenModel, Properties properties)
	{
		super.init(screenModel, properties);
	}
	/**
	 * Free this control.
	 */
	public void free()
	{
		super.free();
		if (this.isOpen())
			this.closeConnection();
	}
	/**
	 * Set this control up to implement these properties.
	 */
	public void setProperties(Properties properties)
	{
		if ("true".equalsIgnoreCase(properties.getProperty(RemoteControlPropertyView.CONTROL_SERVER)))
		{
			if (!m_bRmiServerRunning)
			{
				try {	// This code loads the serial port code which can't be loaded after the RMISecurityManager is loaded.
					Class.forName("javax.comm.CommPortIdentifier");
				} catch (ClassNotFoundException ex)	{
					// Ignore this error
				}

			}
		}
		
		m_parameters = new SerialPropertyModel(properties);		// Set up the serial parameters using the passed in properties
// Start create serial out
		if (this.isOpen())
			this.closeConnection();
		try	{
			this.openConnection();
		} catch (SerialControlException ex)	{
			System.out.println("Error on open Connection: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		super.setProperties(properties);
	}
	/**
	 * Get the current serial line m_parameters.
	 */
	public SerialPropertyModel getParameters()
	{
		return m_parameters;
	}
   /**
    * Attempts to open a serial connection and streams using the parameters
    * in the SerialPropertyModel object. If it is unsuccesfull at any step it
    * returns the port to a closed state, throws a 
    * <code>SerialControlException</code>, and returns.
	 * Gives a timeout of 30 seconds on the portOpen to allow other applications
    * to reliquish the port if have it open and no longer need it.
    */
   public void openConnection() throws SerialControlException {

		// Obtain a CommPortIdentifier object for the port you want to open.
		try {
			portId = 
			 CommPortIdentifier.getPortIdentifier(m_parameters.getPortName());
		} catch (NoSuchPortException e) {
			throw new SerialControlException(e.getMessage());
		}
	
		// Open the port represented by the CommPortIdentifier object. Give
		// the open call a relatively long timeout of 30 seconds to allow
		// a different application to reliquish the port if the user 
		// wants to.
		try {
			sPort = (SerialPort)portId.open("Terminal", 3000);
		} catch (PortInUseException e) {
			throw new SerialControlException(e.getMessage());
		}
	
		// Set the parameters of the connection. If they won't set, close the
		// port before throwing an exception.
		try {
			setConnectionParameters();
		} catch (SerialControlException e) {	
			sPort.close();
			throw e;
		}
	
		// Open the input and output streams for the connection. If they won't
		// open, close the port before throwing an exception.
		try {
			os = sPort.getOutputStream();
			is = sPort.getInputStream();
		} catch (IOException e) {
			sPort.close();
			throw new SerialControlException("Error opening i/o streams");
		}
	
		// Add this object as an event listener for the serial port.
		try {
			sPort.addEventListener(this);
		} catch (TooManyListenersException e) {
			sPort.close();
			throw new SerialControlException("too many listeners added");
		}
	
		// Set notifyOnDataAvailable to true to allow event driven input.
		sPort.notifyOnDataAvailable(true);
	
		// Set notifyOnBreakInterrup to allow event driven break handling.
		sPort.notifyOnBreakInterrupt(true);
	
		// Set receive timeout to allow breaking out of polling loop during
		// input handling.
		try {
			sPort.enableReceiveTimeout(30);
		} catch (UnsupportedCommOperationException e) {
		}
	
		// Add ownership listener to allow ownership event handling.
		portId.addPortOwnershipListener(this);
	
		m_bOpen = true;
    }
    /**
     * Sets the connection parameters to the setting in the parameters object.
     * If set fails return the parameters object to origional settings and
     * throw exception.
     */
    public void setConnectionParameters() throws SerialControlException {

		// Save state of parameters before trying a set.
		int oldBaudRate = sPort.getBaudRate();
		int oldDatabits = sPort.getDataBits();
		int oldStopbits = sPort.getStopBits();
		int oldParity   = sPort.getParity();
		int oldFlowControl = sPort.getFlowControlMode();
	
		// Set connection parameters, if set fails return parameters object
		// to original state.
		try {
			sPort.setSerialPortParams(m_parameters.getBaudRate(),
						  m_parameters.getDatabits(),
						  m_parameters.getStopbits(),
						  m_parameters.getParity());
		} catch (UnsupportedCommOperationException e) {
			m_parameters.setBaudRate(oldBaudRate);
			m_parameters.setDatabits(oldDatabits);
			m_parameters.setStopbits(oldStopbits);
			m_parameters.setParity(oldParity);
			throw new SerialControlException("Unsupported parameter");
		}
	
		// Set flow control.
		try {
			sPort.setFlowControlMode(m_parameters.getFlowControlIn() 
						   | m_parameters.getFlowControlOut());
		} catch (UnsupportedCommOperationException e) {
			throw new SerialControlException("Unsupported flow control");
		}
    }
    /**
     * Close the port and clean up associated elements.
     */
    public void closeConnection() {
		// If port is alread closed just return.
		if (!m_bOpen)
			return;

		// Check to make sure sPort has reference to avoid a NPE.
		if (sPort != null) {
			try {
			// close the i/o streams.
				os.close();
				is.close();
			} catch (IOException e) {
				System.err.println(e);
			}
	
			// Close the port.
			sPort.close();
	
			// Remove the ownership listener.
			portId.removePortOwnershipListener(this);
		}
	
		m_bOpen = false;
    }
    /**
     * Send a one second break signal.
     */
    public void sendBreak()
	{
		sPort.sendBreak(1000);
    }
    /**
     * Reports the open status of the port.
     * @return true if port is open, false if port is closed.
     */
    public boolean isOpen()
	{
		return m_bOpen;
    }
    /**
     * Handles SerialPortEvents. The two types of SerialPortEvents that this
     * program is registered to listen for are DATA_AVAILABLE and BI. During 
     * DATA_AVAILABLE the port buffer is read until it is drained, when no more
     * data is availble and 30ms has passed the method returns. When a BI
     * event occurs the words BREAK RECEIVED are written to the messageAreaIn.
     */
    public void serialEvent(SerialPortEvent e)
	{
		// Create a StringBuffer and int to receive input data.
		StringBuffer inputBuffer = new StringBuffer();
		int newData = 0;
	
		// Determine type of event.
		switch (e.getEventType()) {
	
			// Read data until -1 is returned. If \r is received substitute
			// \n for correct newline handling.
			case SerialPortEvent.DATA_AVAILABLE:
				while (newData != -1) {
					try {
						newData = is.read();
					if (newData == -1) {
					break;
					}
						this.sendCharToModel((char)newData);
					} catch (IOException ex) {
						System.err.println(ex);
						return;
					}
				}
	
			break;
	
			// If break event append BREAK RECEIVED message.
			case SerialPortEvent.BI:
		//x	messageAreaIn.append("\n--- BREAK RECEIVED ---\n");
		}

    }
    /**
     * Handles ownership events. If a PORT_OWNERSHIP_REQUESTED event is
     * received a dialog box is created asking the user if they are 
     * willing to give up the port. No action is taken on other types
     * of ownership events.
     */
    public void ownershipChange(int type) {
		if (type == CommPortOwnershipListener.PORT_OWNERSHIP_REQUESTED) {
	//+	    PortRequestedDialog prd = new PortRequestedDialog(parent);
		}
    }
	/**
	 * Handles the KeyEvent.
	 * Gets the <code>char</char> generated by the <code>KeyEvent</code>,
	 * converts it to an <code>int</code>, writes it to the <code>
	 * OutputStream</code> for the port.
	 */
	public void sendCharToControl(char chChar)
	{
		try {
			os.write((int)chChar);
		} catch (IOException e) {
			System.err.println("OutputStream write error: " + e);
		}
		super.sendCharToControl(chChar);
	}
	/**
	 * Screen that is used to change the properties.
	 */
	public PropertyView getPropertyView(Properties properties)
	{
		return new SerialPropertyView(this, properties);
	}
}
