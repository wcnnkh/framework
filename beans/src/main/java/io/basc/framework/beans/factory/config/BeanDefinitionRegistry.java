package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.factory.BeanDefinitionStoreException;
import io.basc.framework.beans.factory.NoSuchBeanDefinitionException;
import io.basc.framework.util.alias.AliasRegistry;
import io.basc.framework.util.collection.Elements;

public interface BeanDefinitionRegistry extends AliasRegistry {
	void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException;

	void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * Check if this registry contains a bean definition with the given name.
	 * 
	 * @param beanName the name of the bean to look for
	 * @return if this registry contains a bean definition with the given name
	 */
	boolean containsBeanDefinition(String beanName);

	Elements<String> getBeanDefinitionNames();
}
