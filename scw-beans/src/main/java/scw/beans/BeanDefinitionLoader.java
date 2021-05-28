package scw.beans;


public interface BeanDefinitionLoader {
	BeanDefinition load(ConfigurableBeanFactory beanFactory, Class<?> sourceClass,
			BeanDefinitionLoaderChain loaderChain);
}