package shuchaowen.core.beans;

import java.lang.reflect.Method;

import shuchaowen.core.beans.property.PropertiesFactory;

public interface BeanMethod {
	Object invoke(Object bean, BeanFactory beanFactory, PropertiesFactory propertiesFactory) throws Exception;
	
	Method getMethod();
}
