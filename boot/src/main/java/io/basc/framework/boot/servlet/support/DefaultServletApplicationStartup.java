package io.basc.framework.boot.servlet.support;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.servlet.ServletApplicationStartup;
import io.basc.framework.boot.servlet.ServletContextInitialization;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Return;

public class DefaultServletApplicationStartup implements ServletApplicationStartup {
	private Logger logger = LoggerFactory.getLogger(getClass());

	protected Return<Application> getStartup(ServletContext servletContext) throws ServletException {
		Application application = ServletContextUtils.getApplication(servletContext);
		if (application == null) {
			ServletContextUtils.startLogger(logger, servletContext, null, false);
			application = new ServletApplication(servletContext);
			application.init();
			ServletContextUtils.setApplication(servletContext, application);
			return Return.success(application);
		} else {
			return Return.error("已经创建过了", application);
		}
	}

	public Return<Application> start(ServletContext servletContext) throws ServletException {
		Return<Application> startUp = getStartup(servletContext);
		start(servletContext, startUp.get());
		return startUp;
	}

	public final boolean start(final ServletContext servletContext, Application application) throws ServletException {
		String nameToUse = ServletApplicationStartup.class.getName();
		if (servletContext.getAttribute(nameToUse) != null) {
			return false;
		}

		servletContext.setAttribute(nameToUse, true);
		afterStarted(servletContext, application);
		ServletContextUtils.startLogger(logger, servletContext, null, true);
		return true;
	}

	protected void afterStarted(ServletContext servletContext, Application application) throws ServletException {
		for (ServletContextInitialization initialization : application
				.getServiceLoader(ServletContextInitialization.class)) {
			initialization.init(application, servletContext);
		}
	}
}
