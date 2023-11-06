package io.basc.framework.execution.aop.jdk;

import java.lang.reflect.InvocationHandler;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.aop.Proxy;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.ArrayUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 可执行的代理
 * 
 * @author wcnnkh
 *
 */
@Data
@RequiredArgsConstructor
public class JdkProxy implements Proxy {
	private final TypeDescriptor returnTypeDescriptor;
	private final ClassLoader classLoader;
	private final Class<?>[] interfaces;
	private final InvocationHandler invocationHandler;

	public JdkProxy(TypeDescriptor source, Class<?>[] interfaces, ExecutionInterceptor executionInterceptor) {
		this(source, source.getType().getClassLoader(), interfaces,
				new ExecutionInterceptorToInvocationHandler(source, executionInterceptor));
	}

	@Override
	public boolean isExecuted(Class<?>[] types) {
		return ArrayUtils.isEmpty(types);
	}

	@Override
	public Object execute(Class<?>[] types, Object[] args) {
		if (!ArrayUtils.isEmpty(args)) {
			throw new UnsupportedException("Jdk proxy does not support calls with parameters");
		}
		return execute();
	}

	@Override
	public Object execute() {
		return java.lang.reflect.Proxy.newProxyInstance(classLoader, interfaces == null ? new Class<?>[0] : interfaces,
				invocationHandler);
	}

}
