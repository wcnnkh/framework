package io.basc.framework.beans;

import io.basc.framework.util.alias.AliasRegistry;

public interface BeanDefinitionRegistry extends BeanDefinitionFactory, AliasRegistry {

	void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

	void removeBeanDefinition(String beanName);
}
