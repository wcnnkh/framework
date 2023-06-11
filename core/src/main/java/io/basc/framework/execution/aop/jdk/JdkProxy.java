package io.basc.framework.execution.aop.jdk;

import java.lang.reflect.InvocationHandler;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.aop.ExecutionInterceptor;
import io.basc.framework.execution.aop.Proxy;
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
	private final ClassLoader classLoader;
	private final Class<?>[] interfaces;
	private final InvocationHandler invocationHandler;

	public JdkProxy(TypeDescriptor source, Class<?>[] interfaces, ExecutionInterceptor executionInterceptor) {
		this(source.getType().getClassLoader(), interfaces,
				new ExecutionInterceptorToInvocationHandler(source, executionInterceptor));
	}

	@Override
	public boolean isExecuted(Elements<? extends TypeDescriptor> types) {
		return types.isEmpty();
	}

	@Override
	public Object execute(Elements<? extends TypeDescriptor> types, Elements<? extends Object> args) throws Throwable {
		if (!args.isEmpty()) {
			throw new UnsupportedException("Jdk proxy does not support calls with parameters");
		}

		return java.lang.reflect.Proxy.newProxyInstance(classLoader, interfaces == null ? new Class<?>[0] : interfaces,
				invocationHandler);
	}
}
