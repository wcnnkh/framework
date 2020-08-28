package scw.servlet;

import java.util.Enumeration;

import javax.servlet.ServletConfig;

import scw.util.MultiEnumeration;
import scw.value.property.ExtendGetPropertyFactory;

public class ServletConfigPropertyFactory extends ExtendGetPropertyFactory {
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
