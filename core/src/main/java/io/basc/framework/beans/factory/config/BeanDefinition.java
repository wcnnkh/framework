package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.execution.Executor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Elements;

/**
 * bean的定义
 * 
 * @author wcnnkh
 *
 */
public interface BeanDefinition {
	String getName();
	
	String getResourceDescription();

	/**
	 * 执行销毁
	 * 
	 * @param executor
	 * @param bean
	 * @throws BeansException
	 */
	<T> void destroy(Executor executor, T bean) throws BeansException;

	/**
	 * 可选的构造器
	 */
	Elements<? extends Executor> getExecutors();

	/**
	 * Return the originating BeanDefinition, or {@code null} if none.
	 * <p>
	 * Allows for retrieving the decorated bean definition, if any.
	 * <p>
	 * Note that this method returns the immediate originator. Iterate through the
	 * originator chain to find the original BeanDefinition as defined by the user.
	 */
	@Nullable
	BeanDefinition getOriginatingBeanDefinition();

	/**
	 * 作用域
	 * 
	 * @return
	 */
	Scope getScope();

	/**
	 * 执行初始化
	 * 
	 * @param executor
	 * @param bean
	 * @throws BeansException
	 */
	<T> void init(Executor executor, Object bean) throws BeansException;

	/**
	 * 是否是一个单例
	 * 
	 * @return
	 */
	boolean isSingleton();
}
