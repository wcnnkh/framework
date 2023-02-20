package io.basc.framework.boot.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import io.basc.framework.boot.Application;
import io.basc.framework.util.Return;

public interface ServletApplicationStartup {

	Return<Application> start(ServletContext servletContext) throws ServletException;

	boolean start(ServletContext servletContext, Application application) throws ServletException;
}
