package io.basc.framework.beans.support;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.config.BeanFactoryPostProcessor;

public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor{

	void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;

}
