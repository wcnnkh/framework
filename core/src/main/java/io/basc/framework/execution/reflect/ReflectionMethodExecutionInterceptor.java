package io.basc.framework.execution.reflect;

import io.basc.framework.util.element.Elements;

/**
 * 对反射方法的拦截
 * 
 * @author wcnnkh
 *
 */
public interface ReflectionMethodExecutionInterceptor extends ReflectionExecutionInterceptor {

	@Override
	default Object intercept(ReflectionConstructor executor, Elements<? extends Object> args) throws Throwable {
		return executor.execute(args);
	}
}
