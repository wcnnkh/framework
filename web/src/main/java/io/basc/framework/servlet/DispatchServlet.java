package io.basc.framework.servlet;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import io.basc.framework.net.server.dispatch.DispatchServer;

public class DispatchServlet extends GenericServlet {
	private static final long serialVersionUID = 1L;
	private final DispatchServer server = new DispatchServer();

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		servlet
	}

}
