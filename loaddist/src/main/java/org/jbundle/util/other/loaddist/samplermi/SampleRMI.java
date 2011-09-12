/*
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 

 * Copyright © 2011 jbundle.org. All rights reserved.
 */
package org.jbundle.util.other.loaddist.samplermi;

import java.rmi.*;

/**
 * Remote interface that will help test the ServletHandler servlet.
 * This interface will be implemented by the SampleRMIServer.  
 */
public interface SampleRMI extends java.rmi.Remote
{
    /**
     * Test remote method.
     */
    public String justPass(String toPass) throws java.rmi.RemoteException;
    /**
     * Make a new Sample RMI server.
     */
    public SampleRMI makeaNewOne() throws java.rmi.RemoteException;
}

