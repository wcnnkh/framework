package io.basc.framework.context.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import io.basc.framework.beans.factory.config.ConfigurableServices;

public class ConfigurableServletContextInitializer extends ConfigurableServices<ServletContextInitializer>
		implements ServletContextInitializer {

	public ConfigurableServletContextInitializer() {
		setServiceClass(ServletContextInitializer.class);
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		for (ServletContextInitializer initializer : getServices()) {
			initializer.onStartup(servletContext);
		}
	}

}
