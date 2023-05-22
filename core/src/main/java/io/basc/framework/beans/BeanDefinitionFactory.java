package io.basc.framework.beans;

import io.basc.framework.util.Elements;
import io.basc.framework.util.alias.AliasFactory;

public interface BeanDefinitionFactory extends AliasFactory {
	BeanDefinition getBeanDefinition(String beanName);

	/**
	 * Check if this registry contains a bean definition with the given name.
	 * 
	 * @param beanName the name of the bean to look for
	 * @return if this registry contains a bean definition with the given name
	 */
	boolean containsBeanDefinition(String beanName);

	Elements<String> getBeanDefinitionNames();
}
