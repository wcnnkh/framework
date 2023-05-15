package io.basc.framework.factory.support;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.FactoryException;

public interface BeanDefinitionLoader {
	BeanDefinition load(BeanFactory beanFactory, ClassLoader classLoader, String name, BeanDefinitionLoaderChain chain) throws FactoryException;
}
