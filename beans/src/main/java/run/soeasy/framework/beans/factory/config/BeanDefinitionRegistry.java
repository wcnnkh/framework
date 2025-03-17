package run.soeasy.framework.beans.factory.config;

import run.soeasy.framework.beans.factory.BeanDefinitionStoreException;
import run.soeasy.framework.beans.factory.NoSuchBeanDefinitionException;
import run.soeasy.framework.util.alias.AliasRegistry;
import run.soeasy.framework.util.collections.Elements;

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
