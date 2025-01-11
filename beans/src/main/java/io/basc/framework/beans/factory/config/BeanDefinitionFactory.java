package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.factory.NoSuchBeanDefinitionException;
import io.basc.framework.util.alias.AliasFactory;
import io.basc.framework.util.collections.Elements;

public interface BeanDefinitionFactory extends AliasFactory {
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
