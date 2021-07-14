package scw.boot.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import scw.boot.Application;
import scw.util.Status;

public interface ServletApplicationStartup {
	
	Status<Application> start(ServletContext servletContext) throws ServletException;
	
	boolean start(ServletContext servletContext, Application application) throws ServletException;
}
