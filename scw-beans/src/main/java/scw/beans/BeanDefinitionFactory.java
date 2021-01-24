package scw.beans;

import scw.util.alias.AliasFactory;


public interface BeanDefinitionFactory extends AliasFactory{
	BeanDefinition getBeanDefinition(String name);

	BeanDefinition getBeanDefinition(Class<?> clazz);
	
	/**
	 * Check if this registry contains a bean definition with the given name.
	 * @param beanName the name of the bean to look for
	 * @return if this registry contains a bean definition with the given name
	 */
	boolean containsBeanDefinition(String beanName);
	
	String[] getBeanDefinitionNames();
}
