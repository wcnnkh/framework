package io.basc.framework.execution.aop.jdk;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import io.basc.framework.execution.Executable;
import io.basc.framework.execution.ExecutionInterceptor;
import io.basc.framework.util.Elements;
import lombok.Data;

@Data
final class ExecutionInterceptorToInvocationHandler implements InvocationHandler, Serializable {
	private static final long serialVersionUID = 1L;
	private final Executable source;
	private final ExecutionInterceptor executionInterceptor;

	public ExecutionInterceptorToInvocationHandler(Executable source, ExecutionInterceptor executionInterceptor) {
		this.source = source;
		this.executionInterceptor = executionInterceptor;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		JdkProxyMethodExecutor executor = new JdkProxyMethodExecutor(proxy, method);
		return executionInterceptor.intercept(source, executor, Elements.forArray(args));
	}
}
