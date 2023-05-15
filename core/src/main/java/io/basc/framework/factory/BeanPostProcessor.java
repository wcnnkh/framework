package io.basc.framework.factory;

import io.basc.framework.beans.BeanDefinition;

public interface BeanPostProcessor {
	void processPostBean(Object bean, BeanDefinition definition) throws FactoryException;
}
