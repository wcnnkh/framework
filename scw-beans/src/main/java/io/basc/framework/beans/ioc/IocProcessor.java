package io.basc.framework.beans.ioc;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeansException;

public interface IocProcessor {
	void process(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory) throws BeansException;
}
