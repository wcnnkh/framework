package io.basc.framework.execution.reflect;

import io.basc.framework.execution.MethodExecutor;
import io.basc.framework.execution.aop.MethodExecutionInterceptor;
import io.basc.framework.util.element.Elements;

/**
 * 对反射方法的拦截
 * 
 * @author wcnnkh
 *
 */
public interface ReflectionMethodExecutionInterceptor extends MethodExecutionInterceptor {

	@Override
	default Object intercept(MethodExecutor executor, Elements<Object> args) throws Throwable {
		if (executor instanceof ReflectionMethodExecutor) {
			return intercept((ReflectionMethodExecutor) executor, args);
		}
		return executor.execute(args);
	}

	Object intercept(ReflectionMethodExecutor executor, Elements<Object> args) throws Throwable;
}
