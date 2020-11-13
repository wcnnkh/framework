package scw.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import scw.application.Application;
import scw.application.ApplicationUtils;

public class DefaultServletOperations implements ServletOperations {

	public void servletContainerInitializer(ServletContext servletContext, Application application)
			throws ServletException {
		for (ServletContextInitialization initializer : ApplicationUtils
				.loadAllService(ServletContextInitialization.class, application)) {
			initializer.init(application, servletContext);
		}
	}
}
