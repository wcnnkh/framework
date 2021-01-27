package scw.beans;


public interface BeanDefinitionLoaderChain {
	BeanDefinition load(BeanFactory beanFactory, Class<?> sourceClass);
}
