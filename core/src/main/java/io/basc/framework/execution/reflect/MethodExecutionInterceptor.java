package io.basc.framework.execution.reflect;

import io.basc.framework.execution.Executor;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.util.element.Elements;

/**
 * 对方法的拦截
 * 
 * @author wcnnkh
 *
 */
public abstract class MethodExecutionInterceptor implements ExecutionInterceptor {

	@Override
	public Object intercept(Executor executor, Elements<? extends Object> args) throws Throwable {
		if (executor instanceof MethodExecutor) {
			return intercept((MethodExecutor) executor, args);
		}
		return executor.execute(args);
	}

	public abstract Object intercept(MethodExecutor executor, Elements<? extends Object> args) throws Throwable;
}
