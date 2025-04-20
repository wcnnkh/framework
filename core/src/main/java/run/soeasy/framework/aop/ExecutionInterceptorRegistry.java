package run.soeasy.framework.aop;

import lombok.NonNull;
import run.soeasy.framework.core.exe.Execution;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class ExecutionInterceptorRegistry extends ConfigurableServices<ExecutionInterceptor>
		implements ExecutionInterceptor {
	private Execution nextChain;

	public ExecutionInterceptorRegistry() {
		setServiceClass(ExecutionInterceptor.class);
	}

	@Override
	public Object intercept(@NonNull Execution function, @NonNull Object... args) throws Throwable {
		ExecutionInterceptorChain chain = new ExecutionInterceptorChain(iterator(), nextChain);
		return chain.intercept(function, args);
	}

}
