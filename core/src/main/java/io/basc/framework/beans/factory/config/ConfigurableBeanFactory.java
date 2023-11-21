package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.FactoryBean;
import io.basc.framework.beans.factory.HierarchicalBeanFactory;
import io.basc.framework.beans.factory.ListableBeanFactory;
import io.basc.framework.execution.param.ParameterExtractors;

public interface ConfigurableBeanFactory
		extends ListableBeanFactory, HierarchicalBeanFactory, SingletonBeanRegistry, BeanDefinitionRegistry, Lifecycle {
	ParameterExtractors getParameterExtractors();

	void removeFactoryBean(String beanName) throws BeansException;

	void registerFactoryBean(String beanName, FactoryBean<? extends Object> factoryBean) throws BeansException;

	/**
	 * 初始化一个bean
	 * 
	 * @param beanName
	 * @param bean
	 * @throws BeansException
	 */
	void initializationBean(String beanName, Object bean) throws BeansException;

	/**
	 * 销毁一个bean
	 * 
	 * @param beanName
	 * @param bean
	 * @throws BeansException
	 */
	void destroyBean(String beanName, Object bean) throws BeansException;

	/**
	 * 销毁所有单例
	 */
	void destroySingletons();

	BeanPostProcessors getBeanPostProcessors();

	/**
	 * Load or refresh the persistent representation of the configuration, which
	 * might be from Java-based configuration, an XML file, a properties file, a
	 * relational database schema, or some other format.
	 * <p>
	 * As this is a startup method, it should destroy already created singletons if
	 * it fails, to avoid dangling resources. In other words, after invocation of
	 * this method, either all or no singletons at all should be instantiated.
	 * 
	 * @throws BeansException        if the bean factory could not be initialized
	 * @throws IllegalStateException if already initialized and multiple refresh
	 *                               attempts are not supported
	 */
	void refresh() throws BeansException, IllegalStateException;

	@Override
	void start() throws BeansException;

	@Override
	void stop() throws BeansException;

	void close();

	boolean isActive();
}
