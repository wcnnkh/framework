package io.basc.framework.execution.aop;

import io.basc.framework.execution.Function;
import io.basc.framework.execution.Method;
import io.basc.framework.util.element.Elements;

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
