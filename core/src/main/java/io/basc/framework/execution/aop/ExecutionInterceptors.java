package io.basc.framework.execution.aop;

import io.basc.framework.execution.Function;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.NonNull;

@Data
public class ExecutionInterceptors implements ExecutionInterceptor {
	@NonNull
	private final Elements<? extends ExecutionInterceptor> executionInterceptors;
	private Function nextChain;

	@Override
	public Object intercept(Function function, Elements<? extends Object> args) throws Throwable {
		ExecutionInterceptorChain chain = new ExecutionInterceptorChain(executionInterceptors.iterator(), nextChain);
		return chain.intercept(function, args);
	}

}
