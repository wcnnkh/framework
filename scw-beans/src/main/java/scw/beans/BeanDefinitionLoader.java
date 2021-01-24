package scw.beans;


public interface BeanDefinitionLoader {
	BeanDefinition load(BeanFactory beanFactory, Class<?> sourceClass,
			BeanDefinitionLoaderChain loaderChain);
}