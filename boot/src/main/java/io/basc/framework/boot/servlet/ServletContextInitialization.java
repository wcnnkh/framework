package io.basc.framework.boot.servlet;

import io.basc.framework.boot.Application;

import javax.servlet.ServletContext;

public interface ServletContextInitialization {
	void init(Application application, ServletContext servletContext);
}
