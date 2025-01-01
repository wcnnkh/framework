package io.basc.framework.web.servlet;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.beans.factory.config.Configurable;
import io.basc.framework.core.Constants;
import io.basc.framework.web.ServerRequest;
import io.basc.framework.web.ServerResponse;
import io.basc.framework.web.WebServer;

public class WebServlet extends GenericServlet implements Configurable {
	private static final long serialVersionUID = 1L;
	private final WebServer webServer = new WebServer();
	private final DispatchServletConverter dispatchServletConverter = new DispatchServletConverter();
	private boolean configurabled;
	private String charsetName = Constants.UTF_8_NAME;

	@Override
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		if (charsetName != null) {
			req.setCharacterEncoding(charsetName);
			res.setCharacterEncoding(charsetName);
		}

		if (dispatchServletConverter.canConvert(req) && dispatchServletConverter.canConvert(res)) {
			ServerRequest request = dispatchServletConverter.convert(req);
			ServerResponse response = dispatchServletConverter.convert(res);
			webServer.service(request, response);
		} else {
			noService(req, res);
		}
	}

	public String getCharsetName() {
		return charsetName;
	}

	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}

	protected void noService(ServletRequest req, ServletResponse res) throws ServletException, IOException {

	}

	public WebServer getWebServer() {
		return webServer;
	}

	@Override
	public boolean isConfigured() {
		return configurabled;
	}

	public DispatchServletConverter getDispatchServletConverter() {
		return dispatchServletConverter;
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		configurabled = true;
		if (!dispatchServletConverter.isConfigured()) {
			dispatchServletConverter.configure(serviceLoaderFactory);
		}

		if (!webServer.isConfigured()) {
			webServer.configure(serviceLoaderFactory);
		}
	}
}
