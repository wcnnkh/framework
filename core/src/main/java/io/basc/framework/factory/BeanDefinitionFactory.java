package io.basc.framework.factory;

import io.basc.framework.util.alias.AliasFactory;

public interface BeanDefinitionFactory extends AliasFactory {
	BeanDefinition getDefinition(String name);

	BeanDefinition getDefinition(Class<?> clazz);

	/**
	 * Check if this registry contains a bean definition with the given name.
	 * 
	 * @param beanName the name of the bean to look for
	 * @return if this registry contains a bean definition with the given name
	 */
	boolean containsDefinition(String beanName);

	String[] getDefinitionIds();
}
