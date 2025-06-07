package run.soeasy.framework.aop;

import lombok.NonNull;
import run.soeasy.framework.core.invoke.Execution;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class ExecutionInterceptorRegistry extends ConfigurableServices<ExecutionInterceptor>
		implements ExecutionInterceptor {
	private Execution nextChain;

	public ExecutionInterceptorRegistry() {
		setServiceClass(ExecutionInterceptor.class);
	}

	@Override
	public Object intercept(@NonNull Execution function) throws Throwable {
		ExecutionInterceptorChain chain = new ExecutionInterceptorChain(iterator(), nextChain);
		return chain.intercept(function);
	}

}
