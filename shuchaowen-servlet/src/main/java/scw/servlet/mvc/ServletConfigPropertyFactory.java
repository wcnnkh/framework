package scw.servlet.mvc;

import javax.servlet.ServletConfig;

import scw.core.PropertyFactory;

public class ServletConfigPropertyFactory implements PropertyFactory {
	private final ServletConfig servletConfig;

	public ServletConfigPropertyFactory(ServletConfig servletConfig) {
		this.servletConfig = servletConfig;
	}

	public String getProperty(String key) {
		return servletConfig.getInitParameter(key);
	}

	public String getConfigXml() {
		return getProperty("shuchaowen");
	}
}
