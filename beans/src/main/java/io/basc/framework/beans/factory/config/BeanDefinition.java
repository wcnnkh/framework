package io.basc.framework.beans.factory.config;

import java.util.Map;

import io.basc.framework.beans.BeanMapping;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.convert.lang.ValueWrapper;
import io.basc.framework.execution.Executors;
import io.basc.framework.execution.Function;
import io.basc.framework.execution.Method;
import io.basc.framework.execution.param.Parameters;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Name;

/**
 * bean的定义
 * 
 * @author wcnnkh
 *
 */
public interface BeanDefinition extends Executors<Function>, Name {
	String getResourceDescription();

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
	
	Parameters getParameters();

	/**
	 * 是否是一个单例
	 * 
	 * @return
	 */
	boolean isSingleton();

	BeanMapping getBeanMapping();

	Map<String, ValueWrapper> getProperties();

	Elements<Method> getInitMethods();

	Elements<Method> getDestroyMethods();
}
