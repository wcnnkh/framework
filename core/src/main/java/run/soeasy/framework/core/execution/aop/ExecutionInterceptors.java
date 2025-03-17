package run.soeasy.framework.core.execution.aop;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.execution.Function;
import run.soeasy.framework.util.collections.Elements;

@Data
public class ExecutionInterceptors implements ExecutionInterceptor {
	@NonNull
	private final Elements<? extends ExecutionInterceptor> executionInterceptors;
	private Function nextChain;

	@Override
	public Object intercept(@NonNull Function function, @NonNull Object... args) throws Throwable {
		ExecutionInterceptorChain chain = new ExecutionInterceptorChain(executionInterceptors.iterator(), nextChain);
		return chain.intercept(function, args);
	}

}
