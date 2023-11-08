package io.basc.framework.execution.aop;

import io.basc.framework.execution.Executor;
import io.basc.framework.execution.MethodExecutor;
import io.basc.framework.util.element.Elements;

/**
 * 对方法的执行的拦截
 */
public interface MethodExecutionInterceptor extends ExecutionInterceptor {
	@Override
	default Object intercept(Executor executor, Elements<Object> args) throws Throwable {
		if (executor instanceof MethodExecutor) {
			return intercept((MethodExecutor) executor, args);
		}
		return intercept(executor, args);
	}

	Object intercept(MethodExecutor executor, Elements<Object> args) throws Throwable;
}
