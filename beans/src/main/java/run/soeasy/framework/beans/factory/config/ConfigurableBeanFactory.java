package run.soeasy.framework.beans.factory.config;

import run.soeasy.framework.beans.BeansException;
import run.soeasy.framework.beans.factory.FactoryBean;
import run.soeasy.framework.beans.factory.HierarchicalBeanFactory;

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
