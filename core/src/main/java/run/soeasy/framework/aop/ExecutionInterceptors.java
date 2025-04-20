package run.soeasy.framework.aop;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exe.Execution;

@Data
public class ExecutionInterceptors implements ExecutionInterceptor {
	@NonNull
	private final Elements<? extends ExecutionInterceptor> executionInterceptors;
	private Execution nextChain;

	@Override
	public Object intercept(@NonNull Execution function, @NonNull Object... args) throws Throwable {
		ExecutionInterceptorChain chain = new ExecutionInterceptorChain(executionInterceptors.iterator(), nextChain);
		return chain.intercept(function, args);
	}

}
