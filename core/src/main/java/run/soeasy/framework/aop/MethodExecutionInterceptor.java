package run.soeasy.framework.aop;

import lombok.NonNull;
import run.soeasy.framework.core.invoke.Execution;
import run.soeasy.framework.core.invoke.Invocation;

/**
 * 对方法的执行的拦截
 */
public interface MethodExecutionInterceptor extends ExecutionInterceptor {
	@Override
	default Object intercept(@NonNull Execution function, @NonNull Object... args) throws Throwable {
		if (function instanceof Invocation) {
			return intercept((Invocation) function, args);
		}
		return intercept(function, args);
	}

	Object intercept(@NonNull Invocation executor, @NonNull Object... args) throws Throwable;
}
