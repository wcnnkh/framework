package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.BeansException;

public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor{

	void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;

}
