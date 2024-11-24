package io.basc.framework.core.execution.aop;

import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.FunctionWrapper;
import io.basc.framework.util.Elements;
import lombok.NonNull;

public class InterceptableFunction extends FunctionWrapper<Function> {
	@NonNull
	private final ExecutionInterceptor executionInterceptor;

	public InterceptableFunction(Function executor, ExecutionInterceptor executionInterceptor) {
		super(executor);
		this.executionInterceptor = executionInterceptor;
	}

	@Override
	public Object execute(Elements<? extends Object> args) throws Throwable {
		return executionInterceptor.intercept(wrappedTarget, args);
	}
}
