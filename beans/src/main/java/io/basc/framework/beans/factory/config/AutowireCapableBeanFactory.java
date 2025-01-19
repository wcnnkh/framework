package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.core.execution.resolver.ParameterFactory;
import io.basc.framework.core.mapping.PropertyFactory;

public interface AutowireCapableBeanFactory extends BeanFactory, ParameterFactory, PropertyFactory {

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
}
