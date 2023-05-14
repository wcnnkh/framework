package io.basc.framework.exec.aop.cglib;

import java.io.Serializable;
import java.lang.reflect.Method;

import io.basc.framework.exec.Executable;
import io.basc.framework.exec.ExecutionInterceptor;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;
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
		CglibProxyMethodExecutor executor = new CglibProxyMethodExecutor(methodProxy, method, methodProxy);
		Elements<Value> parameters = Elements.forArray(args).map((e) -> Value.of(e));
		return executionInterceptor.intercept(this.source, executor, parameters);
	}
}
