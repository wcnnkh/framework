package io.basc.framework.servlet;

import javax.servlet.ServletContext;

import io.basc.framework.core.convert.Value;
import io.basc.framework.transform.factory.PropertyFactory;
import io.basc.framework.util.collection.CollectionUtils;
import io.basc.framework.util.collection.Elements;

public class ServletContextPropertyFactory implements PropertyFactory {
	private final ServletContext servletContext;

	public ServletContextPropertyFactory(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public Value get(String key) {
		String value = servletContext.getInitParameter(key);
		return Value.of(value);
	}

	@Override
	public Elements<String> keys() {
		return Elements.of(() -> CollectionUtils.toIterator(servletContext.getInitParameterNames()));
	}

	public boolean containsKey(String key) {
		return servletContext.getInitParameter(key) != null;
	}
}
