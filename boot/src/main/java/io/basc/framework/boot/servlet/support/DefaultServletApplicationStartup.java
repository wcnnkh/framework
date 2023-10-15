package io.basc.framework.boot.servlet.support;

import javax.servlet.FilterRegistration;
import javax.servlet.Registration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.boot.Application;
import io.basc.framework.boot.servlet.ServletApplication;
import io.basc.framework.boot.servlet.ServletApplicationStartup;
import io.basc.framework.boot.servlet.ServletContextInitialization;
import io.basc.framework.boot.servlet.ServletContextUtils;
import io.basc.framework.boot.servlet.ServletRegistration;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Return;
import io.basc.framework.util.StringUtils;

public class DefaultServletApplicationStartup implements ServletApplicationStartup {
	private Logger logger = LoggerFactory.getLogger(getClass());

	protected Return<Application> getStartup(ServletContext servletContext) throws ServletException {
		Application application = ServletContextUtils.getApplication(servletContext);
		if (application == null) {
			ServletContextUtils.startLogger(logger, servletContext, null, false);
			application = new ServletApplication(getScop(servletContext), servletContext);
			application.init();
			ServletContextUtils.setApplication(servletContext, application);
			return Return.success(application);
		} else {
			return Return.error("已经创建过了", application);
		}
	}

	private Scope getScop(ServletContext servletContext) {
		String scope = servletContext.getInitParameter("scope");
		return StringUtils.isEmpty(scope) ? Scope.DEFAULT : Scope.getUniqueScope(scope);
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
				.getServiceLoader(ServletContextInitialization.class).getServices()) {
			initialization.init(application, servletContext);
		}
	}

	private void register(ServletContext servletContext, Application application) {
		for(Registration registration : application.getServiceLoader(Registration.class).getServices()) {
			if(registration instanceof FilterRegistration) {
				FilterRegistration filterRegistration = (FilterRegistration) registration;
				
			} else if(registration instanceof ServletRegistration) {
				
			}
		}
	}
}
