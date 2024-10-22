package io.basc.framework.context.servlet.support;

import javax.servlet.ServletContext;

import io.basc.framework.context.servlet.ServletApplicationContext;
import io.basc.framework.context.support.GenericApplicationContext;
import io.basc.framework.servlet.ServletContextPropertyFactory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericServletApplicationContext extends GenericApplicationContext implements ServletApplicationContext {
	private final ServletContext servletContext;

	public GenericServletApplicationContext(ServletContext servletContext) {
		this.servletContext = servletContext;
		getEnvironment().register(new ServletContextPropertyFactory(servletContext));
	}
}