package io.basc.framework.factory;

@FunctionalInterface
public interface BeanFactoryPostProcessor {
	/**
	 * Modify the application context's internal bean factory after its standard
	 * initialization. All bean definitions will have been loaded, but no beans will
	 * have been instantiated yet. This allows for overriding or adding properties
	 * even to eager-initializing beans.
	 * 
	 * @param beanFactory the bean factory used by the application context
	 * @throws BeansException in case of errors
	 */
	void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws FactoryException;
}
