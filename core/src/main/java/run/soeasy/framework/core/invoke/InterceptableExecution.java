package run.soeasy.framework.core.invoke;

import lombok.Data;
import lombok.NonNull;

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
