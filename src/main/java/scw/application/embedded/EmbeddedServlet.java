package scw.application.embedded;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.mvc.servlet.ServletService;

public final class EmbeddedServlet implements Servlet {
	private ServletService service;

	public EmbeddedServlet(ServletService service) {
		this.service = service;
	}

	public void init(ServletConfig config) throws ServletException {
	}

	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		service.service(req, res);
	}

	public void destroy() {
	}

	public ServletConfig getServletConfig() {
		return null;
	}

	public String getServletInfo() {
		return null;
	}

}
