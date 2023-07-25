package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.execution.Executor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.element.Elements;

/**
 * bean的定义
 * 
 * @author wcnnkh
 *
 */
public interface BeanDefinition extends BeanDefinitionLifecycle {
	String getResourceDescription();

	/**
	 * 可选的构造器
	 */
	Elements<? extends Executor> getConstructors();

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
	 * 是否是一个单例
	 * 
	 * @return
	 */
	boolean isSingleton();
}
