package io.basc.framework.beans;

import io.basc.framework.execution.Executor;

public interface BeanPostProcessor {
	void processPostBean(String name, BeanDefinition beanDefinition, Executor executor, Object bean)
			throws BeansException;
}
