package io.basc.framework.boot.servlet.support;

import io.basc.framework.boot.Application;
import io.basc.framework.boot.servlet.FilterRegistration;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.XUtils;

import java.util.Collection;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.Servlet;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class Servlet3ApplicationStartup extends DefaultServletApplicationStartup {
	@Override
	protected void afterStarted(ServletContext servletContext, Application application) throws ServletException {
		for (ServletContextListener listener : application.getServiceLoader(ServletContextListener.class)
				.getServices()) {
			servletContext.addListener(listener);
		}

		for (ServletContainerInitializer initializer : application.getServiceLoader(ServletContainerInitializer.class)
				.getServices()) {
			initializer.onStartup(application.getContextClasses().getServices().toSet(), servletContext);
		}
		super.afterStarted(servletContext, application);
	}
}
