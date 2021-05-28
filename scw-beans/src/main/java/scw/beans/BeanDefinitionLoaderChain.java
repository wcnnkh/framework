package scw.beans;


public interface BeanDefinitionLoaderChain {
	BeanDefinition load(ConfigurableBeanFactory beanFactory, Class<?> sourceClass);
}
