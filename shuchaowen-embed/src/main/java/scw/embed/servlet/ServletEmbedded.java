package scw.embed.servlet;

import javax.servlet.Servlet;

import scw.beans.BeanFactory;
import scw.value.property.PropertyFactory;

public interface ServletEmbedded {
	void init(BeanFactory beanFactory, PropertyFactory propertyFactory, Servlet destroy, Servlet service,
			Class<?> mainClass);

	void destroy();
}
