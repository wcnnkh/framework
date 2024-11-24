package io.basc.framework.core.execution.aop.jdk;

import java.lang.reflect.InvocationHandler;

import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.aop.ExecutionInterceptor;
import io.basc.framework.core.execution.aop.Proxy;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.Elements;
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

	public JdkProxy(Class<?> targetClass, Class<?>[] interfaces, ExecutionInterceptor executionInterceptor) {
		this(TypeDescriptor.valueOf(targetClass), targetClass.getClassLoader(), interfaces,
				new ExecutionInterceptorToInvocationHandler(executionInterceptor));
	}

	@Override
	public boolean canExecuted(Elements<? extends Class<?>> parameterTypes) {
		return parameterTypes.isEmpty();
	}

	@Override
	public final Object execute(Elements<? extends Class<?>> parameterTypes, Elements<? extends Object> args) {
		if (!args.isEmpty() || !parameterTypes.isEmpty()) {
			throw new UnsupportedException("Jdk proxy does not support calls with parameters");
		}
		return java.lang.reflect.Proxy.newProxyInstance(classLoader, interfaces == null ? new Class<?>[0] : interfaces,
				invocationHandler);
	}
	
	@Override
	public MergedAnnotations getAnnotations() {
		return MergedAnnotations.from(returnTypeDescriptor);
	}
}
