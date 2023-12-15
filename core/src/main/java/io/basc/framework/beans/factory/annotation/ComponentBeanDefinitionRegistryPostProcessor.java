package io.basc.framework.beans.factory.annotation;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanDefinitionRegistryPostProcessor;
import io.basc.framework.beans.factory.config.ConfigurableListableBeanFactory;

public abstract class ComponentBeanDefinitionRegistryPostProcessor extends ComponentResolvers
		implements BeanDefinitionRegistryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		registerServiceLoader(beanFactory.getBeanProvider(ComponentResolver.class));
		postProcessBeanDefinitionRegistry(beanFactory);
	}
}
