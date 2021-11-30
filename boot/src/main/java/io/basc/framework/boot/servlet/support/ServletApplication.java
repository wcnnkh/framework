package io.basc.framework.boot.servlet.support;

import javax.servlet.ServletContext;

import io.basc.framework.boot.support.DefaultApplication;
import io.basc.framework.context.servlet.ServletContextPropertyFactory;
import io.basc.framework.context.servlet.ServletContextResourceLoader;
import io.basc.framework.context.support.DirectoryClassesLoader;

public class ServletApplication extends DefaultApplication {

	public ServletApplication(ServletContext servletContext) {
		setClassLoader(servletContext.getClassLoader());
		String webRoot = ServletContextUtils.getWebRoot(servletContext);
		if (webRoot != null) {
			getEnvironment().setWorkPath(webRoot);
			getContextClasses().add(new DirectoryClassesLoader(webRoot, getEnvironment()));
		}

		getEnvironment().addFactory(new ServletContextPropertyFactory(servletContext));
		getEnvironment().addResourceLoader(new ServletContextResourceLoader(servletContext));
	}
}
