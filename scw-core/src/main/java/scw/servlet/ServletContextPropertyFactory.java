package scw.servlet;

import java.util.Enumeration;

import javax.servlet.ServletContext;

import scw.util.MultiEnumeration;
import scw.value.property.ExtendPropertyFactory;

public class ServletContextPropertyFactory extends ExtendPropertyFactory {
	private final ServletContext servletContext;

	public ServletContextPropertyFactory(ServletContext servletContext) {
		super(true, true);
		this.servletContext = servletContext;
	}

	@Override
	protected Object getExtendValue(String key) {
		return servletContext.getInitParameter(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<String> enumerationKeys() {
		return new MultiEnumeration<String>(servletContext.getInitParameterNames(), super.enumerationKeys());
	}
}
