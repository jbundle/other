/*
 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.terminal.control.rmi;

import java.applet.Applet;
import java.awt.Graphics;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.*;
import java.util.Properties;

import java.rmi.RMISecurityManager;
import java.rmi.server.UnicastRemoteObject;

import org.jbundle.terminal.server.*;

public class TestRemote extends UnicastRemoteObject
	implements RmiOut
{
	public TestRemote() throws RemoteException
	{
		super();
	}

	String message = "blank";

	// "obj" is the identifier that we'll use to refer
	// to the remote object that implements the "Hello" interface
	RmiOut obj = null;

	public static void main(String[] args)
	{

		TestRemote hello = null;
		try {
			hello = new TestRemote();
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
		hello.init();
	}

	public void init()
	{
		try {
			InetAddress iNetAddr = InetAddress.getLocalHost();
			String strHostName = iNetAddr.getHostName();
			obj = (RmiOut)Naming.lookup("//" + strHostName + "/HelloServer");
//x			message = obj.sayHello();

			obj.setProperties(this, null);

			while (true)
			{
				obj.sendThisChar('A');
			}

//			System.out.println("Message: " + message);
		} catch (Exception e) {
			System.out.println("HelloApplet exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void setProperties(RmiOut hello, Properties properties) throws RemoteException
	{
		// Not used
	}
	public void sendThisChar(char chChar) throws RemoteException
	{
		System.out.print("" + chChar);
	}

	public void paint(Graphics g)
	{
		g.drawString(message, 25, 50);
	}

	public String sendThisString(String strString,int iFunction,int iModifiers) throws RemoteException {
		return null;	// Not implemented
	}
	public String getCurrentView() throws RemoteException {
		return null;	// Not implemented
	}
	public ModelData getCurrentModel() throws RemoteException {
		return null;	// Not implemented
	}
}
