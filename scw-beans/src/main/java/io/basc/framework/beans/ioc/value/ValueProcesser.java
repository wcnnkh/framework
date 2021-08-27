package io.basc.framework.beans.ioc.value;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.annotation.Value;
import io.basc.framework.mapper.Field;

public interface ValueProcesser {
	void process(BeanDefinition beanDefinition, BeanFactory beanFactory, Object bean, Field field, Value value);
}