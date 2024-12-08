package io.basc.framework.core.execution.aop;

import io.basc.framework.core.execution.Function;
import io.basc.framework.util.spi.ConfigurableServices;
import lombok.NonNull;

public class ExecutionInterceptorRegistry extends ConfigurableServices<ExecutionInterceptor>
		implements ExecutionInterceptor {
	private Function nextChain;

	public ExecutionInterceptorRegistry() {
		setServiceClass(ExecutionInterceptor.class);
	}

	@Override
	public Object intercept(@NonNull Function function, @NonNull Object... args) throws Throwable {
		ExecutionInterceptorChain chain = new ExecutionInterceptorChain(iterator(), nextChain);
		return chain.intercept(function, args);
	}

}
