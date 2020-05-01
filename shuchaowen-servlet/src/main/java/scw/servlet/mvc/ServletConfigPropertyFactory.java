package scw.servlet.mvc;

import java.util.Enumeration;

import javax.servlet.ServletConfig;

import scw.util.value.property.StringValuePropertyFactory;

public class ServletConfigPropertyFactory extends StringValuePropertyFactory {
	private final ServletConfig servletConfig;

	public ServletConfigPropertyFactory(ServletConfig servletConfig) {
		this.servletConfig = servletConfig;
	}

	public String getConfigXml() {
		// 兼容老版本
		return getString("shuchaowen");
	}

	@Override
	protected String getStringValue(String key) {
		return servletConfig.getInitParameter(key);
	}

	@Override
	protected Enumeration<String> internalEnumerationKeys() {
		return servletConfig.getInitParameterNames();
	}
}
