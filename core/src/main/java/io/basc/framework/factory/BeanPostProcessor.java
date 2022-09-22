package io.basc.framework.factory;

public interface BeanPostProcessor {
	void processPostBean(Object bean, BeanDefinition definition) throws FactoryException;
}
