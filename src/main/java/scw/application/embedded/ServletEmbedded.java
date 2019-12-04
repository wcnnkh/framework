package scw.application.embedded;

import javax.servlet.Servlet;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;

public interface ServletEmbedded {
	void init(BeanFactory beanFactory, PropertyFactory propertyFactory, Servlet destroy, Servlet service,
			Class<?> mainClass);

	void destroy();
}
