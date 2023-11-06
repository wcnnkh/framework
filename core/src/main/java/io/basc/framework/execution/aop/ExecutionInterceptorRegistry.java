package io.basc.framework.execution.aop;

import io.basc.framework.execution.Executor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.spi.Services;

public class ExecutionInterceptorRegistry extends Services<ExecutionInterceptor> implements ExecutionInterceptor {
	private final Executor nextChain;

	public ExecutionInterceptorRegistry() {
		this(null);
	}

	public ExecutionInterceptorRegistry(@Nullable Executor nextChain) {
		this.nextChain = nextChain;
	}

	@Override
	public Object intercept(Executor executor, Object[] args) throws Throwable {
		ExecutionInterceptorChain chain = new ExecutionInterceptorChain(getServices().iterator(), nextChain);
		return chain.intercept(executor, args);
	}

}
