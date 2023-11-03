package io.basc.framework.boot.servlet;

import javax.servlet.ServletContext;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.boot.support.DefaultApplication;
import io.basc.framework.io.loader.DirectoryClassesLoader;
import io.basc.framework.servlet.ServletContextPropertyFactory;
import io.basc.framework.servlet.ServletContextResourceLoader;

public class ServletApplication extends DefaultApplication {

	public ServletApplication(ServletContext servletContext) {
		this(ServletContextUtils.getScope(servletContext), servletContext);
	}

	public ServletApplication(Scope scope, ServletContext servletContext) {
		super(scope);
		setClassLoader(servletContext.getClassLoader());
		String webRoot = ServletContextUtils.getWebRoot(servletContext);
		if (webRoot != null) {
			setWorkPath(webRoot);
			DirectoryClassesLoader directoryClassesLoader = new DirectoryClassesLoader(webRoot);
			directoryClassesLoader.setTypeFilter(getConfigurableTypeFilter());
			getContextClasses().getServiceLoaders().register(directoryClassesLoader);
		}

		getProperties().register(new ServletContextPropertyFactory(servletContext));
		getResourceLoader().getResourceLoaders().register(new ServletContextResourceLoader(servletContext));
	}
}
