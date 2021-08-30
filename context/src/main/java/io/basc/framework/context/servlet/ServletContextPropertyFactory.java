package io.basc.framework.context.servlet;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.StringValue;
import io.basc.framework.value.Value;

import java.util.Iterator;

import javax.servlet.ServletContext;

public class ServletContextPropertyFactory implements PropertyFactory {
	private final ServletContext servletContext;

	public ServletContextPropertyFactory(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public Value getValue(String key) {
		String value = servletContext.getInitParameter(key);
		return value == null? null:new StringValue(value);
	}

	public Iterator<String> iterator() {
		return CollectionUtils.toIterator(servletContext.getInitParameterNames());
	}

	public boolean containsKey(String key) {
		return servletContext.getInitParameter(key) != null;
	}
}
