package io.basc.framework.context.servlet;

import javax.servlet.ServletContext;

import io.basc.framework.context.ApplicationContext;

public interface ServletApplicationContext extends ApplicationContext {
	ServletContext getServletContext();
}
