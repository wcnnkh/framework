package scw.aop.cglib;

import java.io.Serializable;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;
import scw.aop.MethodInterceptor;

public class CglibMethodInterceptor implements net.sf.cglib.proxy.MethodInterceptor, Serializable {
	private static final long serialVersionUID = 1L;
	private final Class<?> targetClass;
	private final MethodInterceptor methodInterceptor;

	public CglibMethodInterceptor(Class<?> targetClass, MethodInterceptor methodInterceptor) {
		this.targetClass = targetClass;
		this.methodInterceptor = methodInterceptor;
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
		CglibProxyInvoker invoker = new CglibProxyInvoker(obj, targetClass, method, methodProxy);
		if (methodInterceptor == null) {
			return invoker.invoke(args);
		}
		
		return methodInterceptor.intercept(invoker, args);
	}
}
