package io.basc.framework.execution.aop;

import io.basc.framework.execution.Executor;
import io.basc.framework.util.element.Elements;

public class InterceptableExecutor extends ExecutorWrapper {
	private final ExecutionInterceptor executionInterceptor;

	public InterceptableExecutor(Executor executor, ExecutionInterceptor executionInterceptor) {
		super(executor);
		this.executionInterceptor = executionInterceptor;
	}

	@Override
	public Object execute(Elements<Object> args) throws Throwable {
		return executionInterceptor.intercept(wrappedTarget, args);
	}
}
