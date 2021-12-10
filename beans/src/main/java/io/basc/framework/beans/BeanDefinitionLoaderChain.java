package io.basc.framework.beans;

public interface BeanDefinitionLoaderChain {
	BeanDefinition load(ConfigurableBeanFactory beanFactory, Class<?> sourceClass);
}
