package io.basc.framework.execution.reflect;

import io.basc.framework.execution.Executor;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.util.element.Elements;

public interface ReflectionExecutionInterceptor extends ExecutionInterceptor {
	@Override
	default Object intercept(Executor executor, Elements<Object> args) throws Throwable {
		if (executor instanceof ReflectionMethodExecutor) {
			return intercept((ReflectionMethodExecutor) executor, args);
		} else if (executor instanceof ReflectionConstructor) {
			return intercept((ReflectionConstructor) executor, args);
		}
		return executor.execute(args);
	}

	Object intercept(ReflectionMethodExecutor executor, Elements<Object> args) throws Throwable;

	Object intercept(ReflectionConstructor executor, Elements<Object> args) throws Throwable;
}
