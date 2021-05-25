package scw.boot.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import scw.boot.ConfigurableApplication;
import scw.util.Result;

public interface ServletApplicationStartup {
	
	Result<ConfigurableApplication> start(ServletContext servletContext) throws ServletException;
	
	boolean start(ServletContext servletContext, ConfigurableApplication application) throws ServletException;
}
