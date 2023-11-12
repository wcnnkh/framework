package io.basc.framework.execution.aop.jdk;

import java.lang.reflect.InvocationHandler;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.aop.Proxy;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.element.Elements;
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
	public boolean canExecuted(Elements<Class<?>> parameterTypes) {
		return parameterTypes.isEmpty();
	}

	@Override
	public final Object execute(Elements<Class<?>> parameterTypes, Elements<Object> args) {
		if (!args.isEmpty() || !parameterTypes.isEmpty()) {
			throw new UnsupportedException("Jdk proxy does not support calls with parameters");
		}
		return java.lang.reflect.Proxy.newProxyInstance(classLoader, interfaces == null ? new Class<?>[0] : interfaces,
				invocationHandler);
	}
}
