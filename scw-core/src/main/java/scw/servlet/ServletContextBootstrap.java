package scw.servlet;

import javax.servlet.ServletContext;

import scw.aop.annotation.AopEnable;

@AopEnable(false)
public interface ServletContextBootstrap {
	void init(ServletContext servletContext);
}
