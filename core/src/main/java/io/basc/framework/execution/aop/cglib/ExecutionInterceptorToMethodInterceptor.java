package io.basc.framework.execution.aop.cglib;

import java.io.Serializable;
import java.lang.reflect.Method;

import io.basc.framework.execution.Executable;
import io.basc.framework.execution.ExecutionInterceptor;
import io.basc.framework.util.Elements;
import lombok.Data;
import net.sf.cglib.proxy.MethodProxy;

@Data
final class ExecutionInterceptorToMethodInterceptor implements net.sf.cglib.proxy.MethodInterceptor, Serializable {
	private static final long serialVersionUID = 1L;
	private final Executable source;
	private final ExecutionInterceptor executionInterceptor;

	public ExecutionInterceptorToMethodInterceptor(Executable source, ExecutionInterceptor executionInterceptor) {
		this.source = source;
		this.executionInterceptor = executionInterceptor;
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		CglibProxyMethodExecutor executor = new CglibProxyMethodExecutor(obj, method, methodProxy);
		return executionInterceptor.intercept(this.source, executor, Elements.forArray(args));
	}
}