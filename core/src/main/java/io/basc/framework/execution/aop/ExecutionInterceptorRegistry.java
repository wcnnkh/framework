package io.basc.framework.execution.aop;

import io.basc.framework.execution.Function;
import io.basc.framework.lang.Nullable;
import io.basc.framework.observe.register.ObservableServiceLoader;
import io.basc.framework.util.element.Elements;

public class ExecutionInterceptorRegistry extends ObservableServiceLoader<ExecutionInterceptor>
		implements ExecutionInterceptor {
	private final Function nextChain;

	public ExecutionInterceptorRegistry() {
		this(null);
	}

	public ExecutionInterceptorRegistry(@Nullable Function nextChain) {
		this.nextChain = nextChain;
	}

	@Override
	public Object intercept(Function function, Elements<? extends Object> args) throws Throwable {
		ExecutionInterceptorChain chain = new ExecutionInterceptorChain(getServices().iterator(), nextChain);
		return chain.intercept(function, args);
	}

}
