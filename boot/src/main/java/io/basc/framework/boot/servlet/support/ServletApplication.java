package io.basc.framework.boot.servlet.support;

import io.basc.framework.beans.xml.XmlBeanFactory;
import io.basc.framework.boot.support.DefaultApplication;
import io.basc.framework.context.servlet.ServletContextPropertyFactory;
import io.basc.framework.context.servlet.ServletContextResourceLoader;
import io.basc.framework.context.support.DirectoryClassesLoader;

import javax.servlet.ServletContext;

public class ServletApplication extends DefaultApplication {

	public ServletApplication(ServletContext servletContext) {
		super(getConfigXml(servletContext));
		setClassLoader(servletContext.getClassLoader());
		String webRoot = ServletContextUtils.getWebRoot(servletContext);
		if(webRoot != null){
			getEnvironment().setWorkPath(webRoot);
			getContextClasses().add(new DirectoryClassesLoader(webRoot));
		}
		
		getEnvironment().addFactory(new ServletContextPropertyFactory(servletContext));
		getEnvironment().addResourceLoader(new ServletContextResourceLoader(servletContext));
	}

	/**
	 * 兼容老版本
	 * 
	 * @param servletContext
	 * @return
	 */
	public static String getConfigXml(ServletContext servletContext) {
		String config = servletContext.getInitParameter("shuchaowen");
		if (config == null) {
			config = servletContext.getInitParameter("scw");
		}

		if (config == null) {
			config = servletContext.getInitParameter(XmlBeanFactory.CONFIG_NAME);
		}
		return config;
	}
}
