package io.basc.framework.execution.aop.cglib;

import java.io.Serializable;
import java.lang.reflect.Method;

import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.util.Elements;
import lombok.Data;
import net.sf.cglib.proxy.MethodProxy;

@Data
final class ExecutionInterceptorToMethodInterceptor implements net.sf.cglib.proxy.MethodInterceptor, Serializable {
	private static final long serialVersionUID = 1L;
	private final ExecutionInterceptor executionInterceptor;

	public ExecutionInterceptorToMethodInterceptor(ExecutionInterceptor executionInterceptor) {
		this.executionInterceptor = executionInterceptor;
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		CglibProxyExecutor executor = new CglibProxyExecutor(method, methodProxy);
		executor.setTarget(obj);
		return executionInterceptor.intercept(executor, Elements.forArray(args));
	}
}
