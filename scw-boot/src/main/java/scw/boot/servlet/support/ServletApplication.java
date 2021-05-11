package scw.boot.servlet.support;

import javax.servlet.ServletContext;

import scw.beans.xml.XmlBeanFactory;
import scw.boot.support.DefaultApplication;
import scw.context.servlet.ServletContextPropertyFactory;
import scw.context.servlet.ServletContextResourceLoader;
import scw.context.support.DirectoryClassesLoader;

public class ServletApplication extends DefaultApplication {

	public ServletApplication(ServletContext servletContext) {
		super(getConfigXml(servletContext));
		setClassLoader(servletContext.getClassLoader());
		String webRoot = ServletContextUtils.getWebRoot(servletContext);
		if(webRoot != null){
			getEnvironment().setWorkPath(webRoot);
			getContextClassesLoader().add(new DirectoryClassesLoader(webRoot));
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
