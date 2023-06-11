package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.Executable;
import io.basc.framework.execution.Executables;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Elements;

/**
 * bean的定义
 * 
 * @author wcnnkh
 *
 */
public interface BeanDefinition extends Executables {

	String getResourceDescription();

	/**
	 * 执行依赖
	 * 
	 * @param executor
	 * @param bean
	 * @throws BeansException
	 */
	<T> void dependence(Executable executor, Object bean) throws BeansException;

	/**
	 * 执行销毁
	 * 
	 * @param executor
	 * @param bean
	 * @throws BeansException
	 */
	<T> void destroy(Executable executor, T bean) throws BeansException;

	/**
	 * 可选的构造器
	 */
	@Override
	Elements<? extends Executable> getMembers();

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
	 * 来源
	 */
	@Override
	TypeDescriptor getSource();

	/**
	 * 执行初始化
	 * 
	 * @param executor
	 * @param bean
	 * @throws BeansException
	 */
	<T> void init(Executable executor, Object bean) throws BeansException;

	/**
	 * 是否是一个单例
	 * 
	 * @return
	 */
	boolean isSingleton();
}
