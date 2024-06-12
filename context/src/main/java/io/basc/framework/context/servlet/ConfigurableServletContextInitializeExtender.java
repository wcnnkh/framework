package io.basc.framework.context.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import io.basc.framework.beans.factory.config.ConfigurableServices;

public class ConfigurableServletContextInitializeExtender extends ConfigurableServices<ServletContextInitializeExtender>
		implements ServletContextInitializeExtender {

	public ConfigurableServletContextInitializeExtender() {
		setServiceClass(ServletContextInitializeExtender.class);
	}

	@Override
	public void onStartup(ServletContext servletContext, ServletContextInitializer chain) throws ServletException {
		ChainServletContextInitializer chainServletContextInitializer = new ChainServletContextInitializer(
				getServices().iterator(), chain);
		chainServletContextInitializer.onStartup(servletContext);
	}

}
