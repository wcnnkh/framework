package scw.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import scw.application.Application;

public interface ServletContextInitialization {
	void init(Application application, ServletContext servletContext) throws ServletException;
}
