/*
 * RemoteProcessMonitor.java
 *
 * Created on January 27, 2001, 2:34 AM
 
 * Copyright © 2012 jbundle.org. All rights reserved.
 */
package com.tourstudio.monitor;

import java.rmi.*;
import com.tourstudio.monitor.common.ProcessMonitor;

/** 
 *
 * @author  Administrator
 * @version 
 */
public interface RemoteProcessMonitor
	extends ProcessMonitor, Remote
{

}
