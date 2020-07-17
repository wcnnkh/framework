package scw.beans.ioc.value;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;

public interface ValueProcesser {
	void process(BeanDefinition beanDefinition, BeanFactory beanFactory, PropertyFactory propertyFactory, Object bean, Field field, Value value);
}