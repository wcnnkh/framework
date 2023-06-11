package io.basc.framework.execution.aop.jdk;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.util.Elements;
import lombok.Data;

@Data
final class ExecutionInterceptorToInvocationHandler implements InvocationHandler, Serializable {
	private static final long serialVersionUID = 1L;
	private final TypeDescriptor source;
	private final ExecutionInterceptor executionInterceptor;

	public ExecutionInterceptorToInvocationHandler(TypeDescriptor source, ExecutionInterceptor executionInterceptor) {
		this.source = source;
		this.executionInterceptor = executionInterceptor;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		JdkProxyExecutor executable = new JdkProxyExecutor(source, method, proxy);
		return executionInterceptor.intercept(executable, Elements.forArray(args));
	}
}
