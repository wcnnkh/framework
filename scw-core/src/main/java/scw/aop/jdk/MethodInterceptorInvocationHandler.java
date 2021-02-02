package scw.aop.jdk;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import scw.aop.MethodInterceptor;

public class MethodInterceptorInvocationHandler implements InvocationHandler, Serializable {
	private static final long serialVersionUID = 1L;
	private final Class<?> targetClass;
	private final MethodInterceptor methodInterceptor;

	public MethodInterceptorInvocationHandler(Class<?> targetClass, MethodInterceptor methodInterceptor) {
		this.targetClass = targetClass;
		this.methodInterceptor = methodInterceptor;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		JdkProxyInvoker invoker = new JdkProxyInvoker(proxy, targetClass, method);
		if (methodInterceptor == null) {
			return invoker.invoke(args);
		}
		return methodInterceptor.intercept(invoker, args);
	}
}
