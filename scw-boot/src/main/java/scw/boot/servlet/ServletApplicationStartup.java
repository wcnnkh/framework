package scw.boot.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import scw.boot.Application;
import scw.util.Result;

public interface ServletApplicationStartup {
	
	Result<Application> start(ServletContext servletContext) throws ServletException;
	
	boolean start(ServletContext servletContext, Application application) throws ServletException;
}
