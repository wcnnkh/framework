package scw.aop.support;

import java.lang.reflect.InvocationHandler;
import java.util.Arrays;

import scw.aop.Proxy;
import scw.lang.UnsupportedException;

public class JdkProxy implements Proxy {
	private Class<?> clazz;
	private Class<?>[] interfaces;
	private InvocationHandler invocationHandler;

	public JdkProxy(Class<?> clazz, Class<?>[] interfaces, InvocationHandler invocationHandler) {
		this.clazz = clazz;
		this.interfaces = interfaces;
		this.invocationHandler = invocationHandler;
	}

	public Object create() {
		return java.lang.reflect.Proxy.newProxyInstance(clazz.getClassLoader(),
				interfaces == null ? new Class<?>[0] : interfaces, invocationHandler);
	}

	public Object create(Class<?>[] parameterTypes, Object[] arguments) {
		throw new UnsupportedException(clazz.getName() + "," + Arrays.toString(parameterTypes));
	}
}
