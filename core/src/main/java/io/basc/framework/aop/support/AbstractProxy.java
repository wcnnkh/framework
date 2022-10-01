package io.basc.framework.aop.support;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.aop.Proxy;
import io.basc.framework.factory.InstanceException;

public abstract class AbstractProxy implements Proxy {
	private final Class<?> targetClass;
	private final Class<?>[] interfaces;
	private final MethodInterceptor methodInterceptor;

	public AbstractProxy(Class<?> targetClass, Class<?>[] interfaces, MethodInterceptor methodInterceptor) {
		this.targetClass = targetClass;
		this.interfaces = interfaces;
		this.methodInterceptor = methodInterceptor;
	}

	public Class<?> getSourceClass() {
		return targetClass;
	}

	public Class<?>[] getInterfaces() {
		return interfaces;
	}

	public MethodInterceptor getMethodInterceptor() {
		return methodInterceptor;
	}

	public Object create(Class<?>[] parameterTypes, Object[] params) throws InstanceException {
		if ((parameterTypes == null || parameterTypes.length == 0) && (params == null || params.length == 0)) {
			return create();
		}
		return createInternal(parameterTypes, params);
	}

	protected abstract Object createInternal(Class<?>[] parameterTypes, Object[] params);
}
