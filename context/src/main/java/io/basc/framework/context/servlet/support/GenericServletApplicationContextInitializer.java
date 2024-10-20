package io.basc.framework.context.servlet.support;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import io.basc.framework.context.servlet.ServletApplicationContext;
import io.basc.framework.context.servlet.ServletContextInitializer;
import io.basc.framework.context.servlet.ServletContextUtils;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

public class GenericServletApplicationContextInitializer implements ServletContextInitializer {
	private static Logger logger = LogManager.getLogger(GenericServletApplicationContextInitializer.class);

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		ServletApplicationContext applicationContext = ServletContextUtils.getServletApplicationContext(servletContext);
		if (applicationContext == null) {
			applicationContext = new GenericServletApplicationContext(servletContext);
			ServletContextUtils.setServletApplicationContext(servletContext, applicationContext);
			ServletContextUtils.startLogger(logger, servletContext, null, true);
		}

	}

}
