package scw.application.embedded;

import javax.servlet.Servlet;

import scw.beans.BeanFactory;
import scw.core.PropertiesFactory;

public interface ServletEmbedded {
	void init(BeanFactory beanFactory, PropertiesFactory propertiesFactory, Servlet destroy, Servlet service);

	void destroy();
}
