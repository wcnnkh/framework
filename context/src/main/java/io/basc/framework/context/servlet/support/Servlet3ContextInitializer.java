package io.basc.framework.context.servlet.support;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import io.basc.framework.context.servlet.ServletContextInitializeExtender;
import io.basc.framework.context.servlet.ServletContextInitializer;

public class Servlet3ContextInitializer implements ServletContextInitializeExtender {

	@Override
	public void onStartup(ServletContext servletContext, ServletContextInitializer chain) throws ServletException {
		
	}

}
