package io.basc.framework.boot.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.boot.Application;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.StringUtils;

public class ServletApplicationStartup {
	private static Logger logger = LoggerFactory.getLogger(ServletApplicationStartup.class);

	public void onStartup(ServletContext servletContext, Application application) {
		for (ServletContextInitialization initialization : application
				.getServiceLoader(ServletContextInitialization.class).getServices()) {
			initialization.init(application, servletContext);
		}
		ServletContextUtils.startLogger(logger, servletContext, null, true);
	}

	public Application load(ServletContext servletContext) throws ServletException {
		Application application = ServletContextUtils.getApplication(servletContext);
		if (application == null) {
			ServletContextUtils.startLogger(logger, servletContext, null, false);
			application = new ServletApplication(getScop(servletContext), servletContext);
			application.init();
			ServletContextUtils.setApplication(servletContext, application);
			start(servletContext, application);
			return application;
		}
		return application;
	}

	private Scope getScop(ServletContext servletContext) {
		String scope = servletContext.getInitParameter("scope");
		return StringUtils.isEmpty(scope) ? Scope.DEFAULT : Scope.getUniqueScope(scope);
	}

	public void start(ServletContext servletContext, Application application) throws ServletException {
		for (ServletContextInitialization initialization : application
				.getServiceLoader(ServletContextInitialization.class).getServices()) {
			initialization.init(application, servletContext);
		}
		ServletContextUtils.startLogger(logger, servletContext, null, true);
	}
}
