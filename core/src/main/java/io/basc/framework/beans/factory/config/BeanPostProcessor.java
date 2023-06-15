package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.BeansException;

/**
 * bean的后置处理器
 * 
 * @author wcnnkh
 *
 */
public interface BeanPostProcessor {
	/**
	 * 在初始化之前执行
	 * 
	 * @param bean
	 * @param beanName
	 * @throws BeansException
	 */
	default void postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		// ignore
	}

	/**
	 * 在初始化之后执行
	 * 
	 * @param bean
	 * @param beanName
	 * @throws BeansException
	 */
	default void postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		// ignore
	}

	/**
	 * 在销毁前执行
	 * 
	 * @param bean
	 * @param beanName
	 * @throws BeansException
	 */
	default void postProcessBeforeDestory(Object bean, String beanName) throws BeansException {
		// ignore
	}

	/**
	 * 在销毁后执行
	 * 
	 * @param bean
	 * @param beanName
	 * @throws BeansException
	 */
	default void postProcessAfterDestory(Object bean, String beanName) throws BeansException {
		// ignore
	}
}
