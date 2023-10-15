package io.basc.framework.boot.servlet;

import java.io.IOException;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.beans.factory.config.Configurable;
import io.basc.framework.boot.Application;
import io.basc.framework.lang.Constants;
import io.basc.framework.web.ServerRequest;
import io.basc.framework.web.ServerResponse;
import io.basc.framework.web.WebServer;
import io.basc.framework.web.servlet.DispatchServletConverter;
import io.basc.framework.web.support.DefaultWebServer;

public class DispatcherServlet extends GenericServlet implements Configurable {
	private static final long serialVersionUID = 1L;
	private final WebServer webServer = new DefaultWebServer();
	private final DispatchServletConverter dispatchServletConverter = new DispatchServletConverter();
	private boolean configurabled;
	private String charsetName = Constants.UTF_8_NAME;
	private Application application;
	private boolean isNew;

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

	@Override
	public void init(ServletConfig config) throws ServletException {
		if (application == null) {
			application = ServletContextUtils.getApplication(config.getServletContext());
			if (application == null) {
				application = new ServletApplication(config.getServletContext());
				isNew = true;
			}
		}
		super.init(config);
	}

	@Override
	public void init() throws ServletException {
		if (isNew) {
			application.init();
			// 如果没有配置过就进行配置
			if (!isConfigured()) {
				configure(application);
			}
		}
		super.init();
	}

	@Override
	public void destroy() {
		super.destroy();
		if (isNew) {
			application.destroy();
		}
	}

	public String getCharsetName() {
		return charsetName;
	}

	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}

	protected void noService(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		// ignore
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
