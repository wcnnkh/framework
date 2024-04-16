package io.basc.framework.context.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public interface ServletContextInitializeExtender {
	void onStartup(ServletContext servletContext, ServletContextInitializer chain) throws ServletException;
}
