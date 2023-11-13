package io.basc.framework.context.servlet;

import javax.servlet.ServletContext;

import io.basc.framework.context.config.ApplicationContextInitializer;
import io.basc.framework.context.config.ConfigurableApplicationContext;
import io.basc.framework.io.loader.DirectoryClassesLoader;
import io.basc.framework.servlet.ServletContextPropertyFactory;
import io.basc.framework.servlet.ServletContextResourceLoader;
import lombok.Data;

@Data
public class ServletApplicationContextInitializer implements ApplicationContextInitializer {
	private final ServletContext servletContext;

	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		applicationContext.setClassLoader(servletContext.getClassLoader());
		String webRoot = ServletContextUtils.getWebRoot(servletContext);
		if (webRoot != null) {
			applicationContext.setWorkPath(webRoot);
			DirectoryClassesLoader directoryClassesLoader = new DirectoryClassesLoader(webRoot);
			directoryClassesLoader.setTypeFilter(getConfigurableTypeFilter());
			applicationContext.getContextClasses().getServiceLoaders().register(directoryClassesLoader);
		}

		applicationContext.getProperties().register(new ServletContextPropertyFactory(servletContext));
		applicationContext.getResourceLoader().getResourceLoaders()
				.register(new ServletContextResourceLoader(servletContext));
	}

}
