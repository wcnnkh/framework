package run.soeasy.framework.aop.jdk;

import java.lang.reflect.InvocationHandler;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.aop.Proxy;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.invoke.intercept.ExecutionInterceptor;

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
	public boolean canExecuted(@NonNull Class<?>... parameterTypes) {
		return parameterTypes.length == 0;
	}

	@Override
	public final Object execute(@NonNull Class<?>[] parameterTypes, @NonNull Object... args) {
		if (args.length != 0 || parameterTypes.length != 0) {
			throw new UnsupportedOperationException("Jdk proxy does not support calls with parameters");
		}
		return java.lang.reflect.Proxy.newProxyInstance(classLoader, interfaces == null ? new Class<?>[0] : interfaces,
				invocationHandler);
	}
}
