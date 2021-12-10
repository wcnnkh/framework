package io.basc.framework.beans;

public interface BeanDefinitionLoader {
	BeanDefinition load(ConfigurableBeanFactory beanFactory, Class<?> sourceClass,
			BeanDefinitionLoaderChain loaderChain);
}