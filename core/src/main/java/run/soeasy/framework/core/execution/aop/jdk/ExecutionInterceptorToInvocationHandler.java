package run.soeasy.framework.core.execution.aop.jdk;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import lombok.Data;
import run.soeasy.framework.core.execution.aop.ExecutionInterceptor;
import run.soeasy.framework.util.collection.Elements;

@Data
final class ExecutionInterceptorToInvocationHandler implements InvocationHandler, Serializable {
	private static final long serialVersionUID = 1L;
	private final ExecutionInterceptor executionInterceptor;

	public ExecutionInterceptorToInvocationHandler(ExecutionInterceptor executionInterceptor) {
		this.executionInterceptor = executionInterceptor;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		JdkProxyExecutor executable = new JdkProxyExecutor(method);
		executable.setTarget(proxy);
		return executionInterceptor.intercept(executable, Elements.forArray(args));
	}
}
