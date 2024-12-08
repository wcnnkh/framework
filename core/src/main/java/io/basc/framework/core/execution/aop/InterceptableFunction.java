package io.basc.framework.core.execution.aop;

import io.basc.framework.core.execution.Function;
import io.basc.framework.core.execution.Function.FunctionWrapper;
import lombok.Data;
import lombok.NonNull;

@Data
public class InterceptableFunction<W extends Function> implements FunctionWrapper<W> {
	@NonNull
	private final W source;
	@NonNull
	private final ExecutionInterceptor executionInterceptor;

	@Override
	public Object execute(@NonNull Object... args) throws Throwable {
		return executionInterceptor.intercept(getSource(), args);
	}
}
