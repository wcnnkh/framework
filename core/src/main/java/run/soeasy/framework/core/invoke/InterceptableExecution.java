package run.soeasy.framework.core.invoke;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.invoke.Execution.ExecutionWrapper;

@Data
public class InterceptableExecution<W extends Execution> implements ExecutionWrapper<W> {
	@NonNull
	private final W source;
	@NonNull
	private final ExecutionInterceptor executionInterceptor;

	@Override
	public Object execute(@NonNull Object... args) throws Throwable {
		return executionInterceptor.intercept(getSource(), args);
	}
}
