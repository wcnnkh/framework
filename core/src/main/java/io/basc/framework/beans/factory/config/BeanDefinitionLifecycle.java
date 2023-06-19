package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.BeansException;
import io.basc.framework.execution.Executor;

public interface BeanDefinitionLifecycle {
	/**
	 * 执行销毁
	 * 
	 * @param constructor
	 * @param bean
	 * @throws BeansException
	 */
	void destroy(Executor constructor, Object bean) throws BeansException;

	/**
	 * 执行初始化
	 * 
	 * @param constructor
	 * @param bean
	 * @throws BeansException
	 */
	void init(Executor constructor, Object bean) throws BeansException;
}
