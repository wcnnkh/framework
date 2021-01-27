package scw.context.servlet;

import java.util.Iterator;

import javax.servlet.ServletContext;

import scw.core.utils.CollectionUtils;
import scw.value.StringValue;
import scw.value.Value;
import scw.value.factory.PropertyFactory;

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
