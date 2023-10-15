package io.basc.framework.execution.reflect;

import io.basc.framework.execution.Executor;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.util.element.Elements;

/**
 * 对构造方法的拦截
 * 
 * @author wcnnkh
 *
 */
public abstract class ConstructorExecutionInterceptor implements ExecutionInterceptor {

	@Override
	public Object intercept(Executor executor, Elements<? extends Object> args) throws Throwable {
		if (executor instanceof ReflectionConstructor) {
			return intercept((ReflectionConstructor) executor, args);
		}
		return executor.execute(args);
	}

	public abstract Object intercept(ReflectionConstructor executor, Elements<? extends Object> args) throws Throwable;
}
