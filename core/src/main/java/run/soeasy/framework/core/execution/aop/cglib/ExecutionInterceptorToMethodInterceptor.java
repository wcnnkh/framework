package run.soeasy.framework.core.execution.aop.cglib;

import java.io.Serializable;
import java.lang.reflect.Method;

import lombok.Data;
import net.sf.cglib.proxy.MethodProxy;
import run.soeasy.framework.core.execution.aop.ExecutionInterceptor;
import run.soeasy.framework.util.collections.Elements;

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
