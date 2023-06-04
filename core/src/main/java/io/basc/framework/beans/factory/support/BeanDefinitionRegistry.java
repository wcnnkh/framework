package io.basc.framework.beans.factory.support;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.util.alias.AliasRegistry;

public interface BeanDefinitionRegistry extends AliasRegistry {

	void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);

	void removeBeanDefinition(String beanName);

	BeanDefinition getBeanDefinition(String beanName);

	/**
	 * Check if this registry contains a bean definition with the given name.
	 * 
	 * @param beanName the name of the bean to look for
	 * @return if this registry contains a bean definition with the given name
	 */
	boolean containsBeanDefinition(String beanName);

	String[] getBeanDefinitionNames();
}
