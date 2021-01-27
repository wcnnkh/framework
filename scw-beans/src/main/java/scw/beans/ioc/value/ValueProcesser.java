package scw.beans.ioc.value;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.mapper.Field;

public interface ValueProcesser {
	void process(BeanDefinition beanDefinition, BeanFactory beanFactory, Object bean, Field field, Value value);
}