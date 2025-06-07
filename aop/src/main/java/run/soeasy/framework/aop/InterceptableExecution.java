package run.soeasy.framework.aop;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.invoke.Execution;
import run.soeasy.framework.core.invoke.ExecutionWrapper;

@Data
public class InterceptableExecution<W extends Execution> implements ExecutionWrapper<W> {
	@NonNull
	private final W source;
	@NonNull
	private final ExecutionInterceptor executionInterceptor;

	@Override
	public Object execute() throws Throwable {
		return executionInterceptor.intercept(source);
	}
}
