package scw.beans.ioc;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.value.property.PropertyFactory;

public interface IocProcessor {
	void process(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory, PropertyFactory propertyFactory) throws Exception;
}
