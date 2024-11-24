package io.basc.framework.core.execution.aop;

import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.Method;
import io.basc.framework.util.Elements;

/**
 * 对方法的执行的拦截
 */
public interface MethodExecutionInterceptor extends ExecutionInterceptor {
	@Override
	default Object intercept(Function function, Elements<? extends Object> args) throws Throwable {
		if (function instanceof Method) {
			return intercept((Method) function, args);
		}
		return intercept(function, args);
	}

	Object intercept(Method executor, Elements<? extends Object> args) throws Throwable;
}
