package scw.beans;

import java.lang.reflect.Method;

import scw.util.value.property.PropertyFactory;

public interface BeanMethod {
	Object invoke(Object bean, BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception;
	
	Method getMethod();
}
