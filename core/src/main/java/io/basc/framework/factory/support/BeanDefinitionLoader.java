package io.basc.framework.factory.support;

import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.FactoryException;

public interface BeanDefinitionLoader {
	BeanDefinition load(BeanFactory beanFactory, String name, BeanDefinitionLoaderChain chain) throws FactoryException;
}
