package scw.embed.servlet;

import javax.servlet.Servlet;

import scw.application.MainArgs;
import scw.beans.BeanFactory;
import scw.beans.annotation.AopEnable;
import scw.value.property.PropertyFactory;

@AopEnable(false)
public interface ServletEmbedded {
	void init(BeanFactory beanFactory, PropertyFactory propertyFactory, Servlet destroy, Servlet service,
			Class<?> mainClass, MainArgs args) throws Exception;

	void destroy() throws Exception;
}
