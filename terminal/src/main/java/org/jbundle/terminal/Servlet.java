/*
 * Servlet.java
 * Copyright (c) 2000 jbundle.org. All Rights Reserved.
 */
package org.jbundle.terminal;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet - This is just a simple class to make it easier to start the overidden servlet class.
 */
public class Servlet extends org.jbundle.terminal.servlet.HtmlViewServlet {

	private static final long serialVersionUID = 1L;

public void doGet(HttpServletRequest req, HttpServletResponse res)
	  throws ServletException, IOException {
	  super.doGet(req, res);
  }

}
