package io.basc.framework.beans.config;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.Scope;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executable;
import io.basc.framework.execution.Executor;
import io.basc.framework.util.Elements;

/**
 * bean的定义
 * 
 * @author wcnnkh
 *
 */
public interface BeanDefinition extends Executable {

	/**
	 * 来源
	 */
	@Override
	TypeDescriptor getSource();

	/**
	 * 可选的构造器
	 */
	@Override
	Elements<? extends Executor> getExecutors();

	/**
	 * 是否是一个单例
	 * 
	 * @return
	 */
	boolean isSingleton();

	/**
	 * 作用域
	 * 
	 * @return
	 */
	Scope getScope();

	/**
	 * 执行依赖
	 * 
	 * @param executor
	 * @param bean
	 * @throws BeansException
	 */
	<T> void dependence(Executor executor, Object bean) throws BeansException;

	/**
	 * 执行初始化
	 * 
	 * @param executor
	 * @param bean
	 * @throws BeansException
	 */
	<T> void init(Executor executor, Object bean) throws BeansException;

	/**
	 * 执行销毁
	 * 
	 * @param executor
	 * @param bean
	 * @throws BeansException
	 */
	<T> void destroy(Executor executor, T bean) throws BeansException;
}
