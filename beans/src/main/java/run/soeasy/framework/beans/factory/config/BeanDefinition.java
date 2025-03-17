package run.soeasy.framework.beans.factory.config;

import run.soeasy.framework.beans.BeanMapping;
import run.soeasy.framework.beans.factory.Scope;
import run.soeasy.framework.core.convert.transform.stereotype.Properties;
import run.soeasy.framework.core.execution.ExecutionStrategy;
import run.soeasy.framework.core.execution.Function;
import run.soeasy.framework.core.execution.Method;
import run.soeasy.framework.util.alias.Named;
import run.soeasy.framework.util.collections.Elements;

/**
 * bean的定义
 * 
 * @author wcnnkh
 *
 */
public interface BeanDefinition extends Named {
	BeanMapping getBeanMappingDescriptor();

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
