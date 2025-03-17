package run.soeasy.framework.core.execution.aop;

import lombok.NonNull;
import run.soeasy.framework.core.execution.Function;
import run.soeasy.framework.core.execution.Method;

/**
 * 对方法的执行的拦截
 */
public interface MethodExecutionInterceptor extends ExecutionInterceptor {
	@Override
	default Object intercept(@NonNull Function function, @NonNull Object... args) throws Throwable {
		if (function instanceof Method) {
			return intercept((Method) function, args);
		}
		return intercept(function, args);
	}

	Object intercept(@NonNull Method executor, @NonNull Object... args) throws Throwable;
}
