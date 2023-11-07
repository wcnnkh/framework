package io.basc.framework.boot.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import io.basc.framework.beans.factory.config.Configurable;
import io.basc.framework.boot.Application;
import io.basc.framework.web.servlet.WebServlet;

public class DispatcherServlet extends WebServlet implements Configurable {
	private static final long serialVersionUID = 1L;
	private Application application;
	private boolean isNew;

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
}
