package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.FactoryBean;
import io.basc.framework.beans.factory.HierarchicalBeanFactory;

public interface ConfigurableBeanFactory
		extends HierarchicalBeanFactory, SingletonBeanRegistry, AutowireCapableBeanFactory {
	void removeFactoryBean(String beanName) throws BeansException;

	void registerFactoryBean(String beanName, FactoryBean<? extends Object> factoryBean) throws BeansException;

	/**
	 * 销毁所有单例
	 */
	void destroySingletons();

	BeanPostProcessors getBeanPostProcessors();
}
