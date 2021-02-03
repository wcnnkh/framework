package scw.aop.jdk;

import java.lang.reflect.InvocationHandler;
import java.util.Arrays;

import scw.aop.MethodInterceptor;
import scw.aop.support.AbstractProxy;
import scw.lang.NotSupportedException;

public class JdkProxy extends AbstractProxy {
	private InvocationHandler invocationHandler;

	public JdkProxy(Class<?> clazz, Class<?>[] interfaces, MethodInterceptor methodInterceptor) {
		super(clazz, interfaces, methodInterceptor);
		this.invocationHandler = new MethodInterceptorInvocationHandler(clazz, methodInterceptor);
	}

	public Object create() {
		Class<?>[] interfaces = getInterfaces();
		return java.lang.reflect.Proxy.newProxyInstance(getTargetClass().getClassLoader(),
				interfaces == null ? new Class<?>[0] : interfaces, invocationHandler);
	}

	public Object createInternal(Class<?>[] parameterTypes, Object[] arguments) {
		throw new NotSupportedException(getTargetClass().getName() + "," + Arrays.toString(parameterTypes));
	}
}
