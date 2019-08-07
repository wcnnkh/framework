package scw.servlet;

import javax.servlet.ServletConfig;

import scw.beans.property.XmlPropertyFactory;
import scw.core.PropertyFactory;
import scw.core.utils.SystemPropertyUtils;

public class ServletConfigPropertyFactory implements PropertyFactory {
	private final ServletConfig servletConfig;
	private final PropertyFactory propertyFactory;
	private final String configXml;

	public ServletConfigPropertyFactory(ServletConfig servletConfig) {
		this.servletConfig = servletConfig;
		this.configXml = getServletConfig("shuchaowen");
		this.propertyFactory = new XmlPropertyFactory(this.configXml);
	}

	public String getConfig(String name) {
		String value = servletConfig.getInitParameter(name);
		if (value == null) {
			value = SystemPropertyUtils.getProperty(name);
		}
		return value;
	}

	public String getServletConfig(String key) {
		return servletConfig.getInitParameter(key);
	}

	public String getProperty(String key) {
		String value = null;
		if (propertyFactory != null) {
			value = propertyFactory.getProperty(key);
		}

		if (value == null) {
			value = getServletConfig(key);
		}
		return value;
	}

	public String getConfigXml() {
		return configXml;
	}
}
