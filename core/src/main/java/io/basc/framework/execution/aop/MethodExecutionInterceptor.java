package io.basc.framework.execution.aop;

import io.basc.framework.execution.Executor;
import io.basc.framework.execution.MethodExecutor;

public interface MethodExecutionInterceptor extends ExecutionInterceptor {
	@Override
	default Object intercept(Executor executor, Object[] args) throws Throwable {
		if (executor instanceof MethodExecutor) {
			return intercept((MethodExecutor) executor, args);
		}
		return intercept(executor, args);
	}

	Object intercept(MethodExecutor executor, Object[] args) throws Throwable;
}
