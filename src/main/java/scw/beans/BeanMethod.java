package scw.beans;

import java.lang.reflect.Method;

import scw.core.PropertiesFactory;

public interface BeanMethod {
	Object invoke(Object bean, BeanFactory beanFactory, PropertiesFactory propertiesFactory) throws Exception;
	
	Method getMethod();
}
