package io.basc.framework.boot.servlet.support;

import javax.servlet.ServletContext;

import io.basc.framework.boot.support.DefaultApplication;
import io.basc.framework.io.DirectoryClassesLoader;
import io.basc.framework.servlet.ServletContextPropertyFactory;
import io.basc.framework.servlet.ServletContextResourceLoader;

public class ServletApplication extends DefaultApplication {

	public ServletApplication(ServletContext servletContext) {
		setClassLoader(servletContext.getClassLoader());
		String webRoot = ServletContextUtils.getWebRoot(servletContext);
		if (webRoot != null) {
			setWorkPath(webRoot);
			DirectoryClassesLoader directoryClassesLoader = new DirectoryClassesLoader(webRoot);
			directoryClassesLoader.setTypeFilter(getContextResolver());
			getContextClasses().getServiceLoaders().register(directoryClassesLoader);
		}

		getProperties().getPropertyFactories().register(new ServletContextPropertyFactory(servletContext));
		getResourceLoader().getResourceLoaders().register(new ServletContextResourceLoader(servletContext));
	}
}
