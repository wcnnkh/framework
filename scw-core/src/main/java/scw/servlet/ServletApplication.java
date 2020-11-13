package scw.servlet;

import javax.servlet.ServletConfig;

import scw.application.CommonApplication;

public class ServletApplication extends CommonApplication {

	public ServletApplication(ServletConfig servletConfig) {
		super(getConfigXml(servletConfig));
		getPropertyFactory().addLastBasePropertyFactory(new ServletConfigPropertyFactory(servletConfig));
	}

	/**
	 * 兼容老版本
	 * 
	 * @param servletConfig
	 * @return
	 */
	public static String getConfigXml(ServletConfig servletConfig) {
		String config = servletConfig.getInitParameter("shuchaowen");
		if (config == null) {
			config = servletConfig.getInitParameter("scw");
		}

		if (config == null) {
			config = servletConfig.getInitParameter("beans");
		}

		return config;
	}
}
