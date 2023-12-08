package io.basc.framework.execution.reflect;

import io.basc.framework.execution.Executor;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.util.element.Elements;

public interface ReflectionExecutionInterceptor extends ExecutionInterceptor {
	@Override
	default Object intercept(Executor executor, Elements<? extends Object> args) throws Throwable {
		if (executor instanceof ReflectionMethod) {
			return intercept((ReflectionMethod) executor, args);
		} else if (executor instanceof ReflectionConstructor) {
			return intercept((ReflectionConstructor) executor, args);
		}
		return executor.execute(args);
	}

	Object intercept(ReflectionMethod executor, Elements<? extends Object> args) throws Throwable;

	Object intercept(ReflectionConstructor executor, Elements<? extends Object> args) throws Throwable;
}
