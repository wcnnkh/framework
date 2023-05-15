package io.basc.framework.execution.aop;

import io.basc.framework.execution.ExecutionInterceptor;
import io.basc.framework.util.ServiceRegistry;

public class Aop extends Proxies {
	private ServiceRegistry<ExecutionInterceptor> executionInterceptors = new ServiceRegistry<>();

	public ServiceRegistry<ExecutionInterceptor> getExecutionInterceptors() {
		return executionInterceptors;
	}

	public void setExecutionInterceptors(ServiceRegistry<ExecutionInterceptor> executionInterceptors) {
		this.executionInterceptors = executionInterceptors;
	}
}
