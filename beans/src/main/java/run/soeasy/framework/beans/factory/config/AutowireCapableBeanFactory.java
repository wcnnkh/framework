package run.soeasy.framework.beans.factory.config;

import run.soeasy.framework.beans.BeansException;
import run.soeasy.framework.beans.factory.BeanFactory;
import run.soeasy.framework.core.convert.transform.stereotype.PropertyFactory;
import run.soeasy.framework.core.execution.resolver.ParameterFactory;

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
