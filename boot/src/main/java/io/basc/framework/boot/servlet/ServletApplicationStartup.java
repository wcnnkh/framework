package io.basc.framework.boot.servlet;

import io.basc.framework.boot.Application;
import io.basc.framework.util.Status;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public interface ServletApplicationStartup {
	
	Status<Application> start(ServletContext servletContext) throws ServletException;
	
	boolean start(ServletContext servletContext, Application application) throws ServletException;
}
