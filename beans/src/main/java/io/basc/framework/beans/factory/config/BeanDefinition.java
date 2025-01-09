package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.BeanMappingDescriptor;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.core.execution.ExecutionStrategy;
import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.Method;
import io.basc.framework.core.mapping.Properties;
import io.basc.framework.util.alias.Named;
import io.basc.framework.util.collection.Elements;

/**
 * bean的定义
 * 
 * @author wcnnkh
 *
 */
public interface BeanDefinition extends Named {
	BeanMappingDescriptor getBeanMappingDescriptor();

	ExecutionStrategy<Function> getExecutionStrategy();

	Elements<Method> getDestroyMethods();

	Elements<Method> getInitMethods();

	/**
	 * Return the originating BeanDefinition, or {@code null} if none.
	 * <p>
	 * Allows for retrieving the decorated bean definition, if any.
	 * <p>
	 * Note that this method returns the immediate originator. Iterate through the
	 * originator chain to find the original BeanDefinition as defined by the user.
	 */
	BeanDefinition getOriginatingBeanDefinition();

	Properties getProperties();

	String getResourceDescription();

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
