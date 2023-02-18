package io.basc.framework.aop.jdk;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.aop.support.AbstractProxy;
import io.basc.framework.lang.UnsupportedException;

import java.lang.reflect.InvocationHandler;
import java.util.Arrays;

public class JdkProxy extends AbstractProxy {
	private InvocationHandler invocationHandler;

	public JdkProxy(Class<?> clazz, Class<?>[] interfaces, MethodInterceptor methodInterceptor) {
		super(clazz, interfaces, methodInterceptor);
		this.invocationHandler = new MethodInterceptorInvocationHandler(clazz, methodInterceptor);
	}

	public Object create() {
		Class<?>[] interfaces = getInterfaces();
		return java.lang.reflect.Proxy.newProxyInstance(getSourceClass().getClassLoader(),
				interfaces == null ? new Class<?>[0] : interfaces, invocationHandler);
	}

	public Object createInternal(Class<?>[] parameterTypes, Object[] arguments) {
		throw new UnsupportedException(getSourceClass().getName() + "," + Arrays.toString(parameterTypes));
	}
}
