package io.basc.framework.aop.cglib;

import io.basc.framework.core.reflect.DefaultMethodInvoker;
import io.basc.framework.lang.NestedExceptionUtils;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodProxy;

public class CglibProxyInvoker extends DefaultMethodInvoker {
	private static final long serialVersionUID = 1L;
	private final MethodProxy methodProxy;

	public CglibProxyInvoker(Object proxy, Class<?> targetClass, Method method, MethodProxy methodProxy) {
		super(proxy, targetClass, method);
		this.methodProxy = methodProxy;
	}

	public Object invoke(Object... args) throws Throwable {
		try {
			return methodProxy.invokeSuper(getInstance(), args);
		} catch (Throwable e) {
			throw NestedExceptionUtils.excludeInvalidNestedExcpetion(e);
		}
	}
}
