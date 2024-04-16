package io.basc.framework.context.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public interface ServletContextInitializer {
	void onStartup(ServletContext servletContext) throws ServletException;
}
