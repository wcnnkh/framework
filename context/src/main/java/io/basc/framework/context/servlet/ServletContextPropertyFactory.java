package io.basc.framework.context.servlet;

import java.util.Iterator;

import javax.servlet.ServletContext;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.Value;

public class ServletContextPropertyFactory implements PropertyFactory {
	private final ServletContext servletContext;

	public ServletContextPropertyFactory(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public Value get(String key) {
		String value = servletContext.getInitParameter(key);
		return Value.of(value);
	}

	public Iterator<String> iterator() {
		return CollectionUtils.toIterator(servletContext.getInitParameterNames());
	}

	public boolean containsKey(String key) {
		return servletContext.getInitParameter(key) != null;
	}
}
