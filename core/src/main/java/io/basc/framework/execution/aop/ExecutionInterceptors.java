package io.basc.framework.execution.aop;

import io.basc.framework.execution.Executor;
import io.basc.framework.util.element.Elements;
import lombok.Data;

@Data
public class ExecutionInterceptors implements ExecutionInterceptor {
	private final Elements<? extends ExecutionInterceptor> executionInterceptors;
	private Executor nextChain;

	@Override
	public Object intercept(Executor executor, Elements<? extends Object> args) throws Throwable {
		ExecutionInterceptorChain chain = new ExecutionInterceptorChain(executionInterceptors.iterator(), nextChain);
		return chain.intercept(executor, args);
	}

}
