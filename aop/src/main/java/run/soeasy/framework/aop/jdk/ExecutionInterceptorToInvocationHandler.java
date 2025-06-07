package run.soeasy.framework.aop.jdk;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import lombok.Data;
import run.soeasy.framework.aop.ExecutionInterceptor;
import run.soeasy.framework.core.invoke.CustomizeInvocation;
import run.soeasy.framework.core.invoke.Invocation;

@Data
final class ExecutionInterceptorToInvocationHandler implements InvocationHandler, Serializable {
	private static final long serialVersionUID = 1L;
	private final ExecutionInterceptor executionInterceptor;

	public ExecutionInterceptorToInvocationHandler(ExecutionInterceptor executionInterceptor) {
		this.executionInterceptor = executionInterceptor;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Invocation invocation = new CustomizeInvocation<>(new JdkProxyMethod(method), args);
		invocation.setTarget(proxy);
		return executionInterceptor.intercept(invocation);
	}
}
