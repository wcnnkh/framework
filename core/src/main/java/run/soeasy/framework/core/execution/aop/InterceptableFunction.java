package run.soeasy.framework.core.execution.aop;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.execution.Function;
import run.soeasy.framework.core.execution.Function.FunctionWrapper;

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
