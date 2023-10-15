package io.basc.framework.execution.reflect;

import io.basc.framework.execution.Executor;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.util.element.Elements;

/**
 * 对方法的拦截
 * 
 * @author wcnnkh
 *
 */
public interface MethodExecutionInterceptor extends ExecutionInterceptor {

	@Override
	default Object intercept(Executor executor, Elements<? extends Object> args) throws Throwable {
		if (executor instanceof ReflectionMethodExecutor) {
			return intercept((ReflectionMethodExecutor) executor, args);
		}
		return executor.execute(args);
	}

	Object intercept(ReflectionMethodExecutor executor, Elements<? extends Object> args) throws Throwable;
}
