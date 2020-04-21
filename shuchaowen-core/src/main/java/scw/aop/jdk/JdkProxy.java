package scw.aop.jdk;

import java.lang.reflect.InvocationHandler;
import java.util.Arrays;

import scw.aop.AbstractProxy;
import scw.lang.UnsupportedException;

public class JdkProxy extends AbstractProxy {
	private Class<?>[] interfaces;
	private InvocationHandler invocationHandler;

	public JdkProxy(Class<?> clazz, Class<?>[] interfaces, InvocationHandler invocationHandler) {
		super(clazz);
		this.interfaces = interfaces;
		this.invocationHandler = invocationHandler;
	}

	public Object create() {
		return java.lang.reflect.Proxy.newProxyInstance(getTargetClass().getClassLoader(),
				interfaces == null ? new Class<?>[0] : interfaces, invocationHandler);
	}

	public Object createInternal(Class<?>[] parameterTypes, Object[] arguments) {
		throw new UnsupportedException(getTargetClass().getName() + "," + Arrays.toString(parameterTypes));
	}
}
