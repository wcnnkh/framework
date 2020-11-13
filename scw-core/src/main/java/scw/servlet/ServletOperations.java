package scw.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import scw.application.Application;

public interface ServletOperations {
	void servletContainerInitializer(ServletContext servletContext, Application application) throws ServletException;
}
