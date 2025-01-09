package io.basc.framework.core.execution.aop;

import io.basc.framework.core.execution.Function;
import io.basc.framework.util.collection.Elements;
import lombok.Data;
import lombok.NonNull;

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
