package scw.beans;

import java.lang.reflect.Method;

import scw.core.PropertyFactory;

public interface BeanMethod {
	Object invoke(Object bean, BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception;
	
	Method getMethod();
}
