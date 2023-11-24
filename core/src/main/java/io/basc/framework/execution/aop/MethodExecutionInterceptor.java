package io.basc.framework.execution.aop;

import io.basc.framework.execution.Executor;
import io.basc.framework.execution.Method;
import io.basc.framework.util.element.Elements;

/**
 * 对方法的执行的拦截
 */
public interface MethodExecutionInterceptor extends ExecutionInterceptor {
	@Override
	default Object intercept(Executor executor, Elements<Object> args) throws Throwable {
		if (executor instanceof Method) {
			return intercept((Method) executor, args);
		}
		return intercept(executor, args);
	}

	Object intercept(Method executor, Elements<Object> args) throws Throwable;
}
