package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.FactoryBean;
import io.basc.framework.beans.factory.HierarchicalBeanFactory;
import io.basc.framework.beans.factory.ListableBeanFactory;

public interface ConfigurableBeanFactory
		extends ListableBeanFactory, HierarchicalBeanFactory, SingletonBeanRegistry, BeanDefinitionRegistry {
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
}
