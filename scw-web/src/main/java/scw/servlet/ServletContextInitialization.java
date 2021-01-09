package scw.servlet;

import javax.servlet.ServletContext;

import scw.boot.Application;

public interface ServletContextInitialization {
	void init(Application application, ServletContext servletContext);
}
