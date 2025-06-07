package run.soeasy.framework.aop;

import lombok.NonNull;
import run.soeasy.framework.core.execute.Execution;
import run.soeasy.framework.core.execute.Invocation;

/**
 * 对方法的执行的拦截
 */
public interface InvocationInterceptor extends ExecutionInterceptor {
	@Override
	default Object intercept(@NonNull Execution execution) throws Throwable {
		if (execution instanceof Invocation) {
			return intercept((Invocation) execution);
		}
		return intercept(execution);
	}

	Object intercept(@NonNull Invocation invocation) throws Throwable;
}
