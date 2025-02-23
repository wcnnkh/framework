package io.basc.framework.context.servlet;

import javax.servlet.ServletContext;

import io.basc.framework.core.convert.Source;
import io.basc.framework.transform.factory.PropertyFactory;
import io.basc.framework.util.collections.CollectionUtils;
import io.basc.framework.util.collections.Elements;

public class ServletContextPropertyFactory implements PropertyFactory {
	private final ServletContext servletContext;

	public ServletContextPropertyFactory(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public Source get(String key) {
		String value = servletContext.getInitParameter(key);
		return Source.of(value);
	}

	@Override
	public Elements<String> keys() {
		return Elements.of(() -> CollectionUtils.toIterator(servletContext.getInitParameterNames()));
	}

	public boolean containsKey(String key) {
		return servletContext.getInitParameter(key) != null;
	}
}
