package io.basc.framework.exec.aop.jdk;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import io.basc.framework.exec.Executable;
import io.basc.framework.exec.ExecutionInterceptor;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;
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
		Elements<Value> parameters = Elements.forArray(args).map((e) -> Value.of(e));
		return executionInterceptor.intercept(source, executor, parameters);
	}
}
