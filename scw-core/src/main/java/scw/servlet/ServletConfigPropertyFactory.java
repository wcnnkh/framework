package scw.servlet;

import java.util.Enumeration;

import javax.servlet.ServletConfig;

import scw.util.MultiEnumeration;
import scw.value.property.ExtendPropertyFactory;

public class ServletConfigPropertyFactory extends ExtendPropertyFactory {
	private final ServletConfig servletConfig;

	public ServletConfigPropertyFactory(ServletConfig servletConfig) {
		super(true, true);
		this.servletConfig = servletConfig;
	}

	public String getConfigXml() {
		// 兼容老版本
		return getString("shuchaowen");
	}
	
	@Override
	protected Object getExtendValue(String key) {
		return servletConfig.getInitParameter(key);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<String> enumerationKeys() {
		return new MultiEnumeration<String>(servletConfig.getInitParameterNames(), super.enumerationKeys());
	}
}
