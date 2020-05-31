package scw.aop;

import java.lang.reflect.Method;

import scw.core.reflect.ReflectionUtils;

public final class ReflectInvoker extends InstanceInvoker {
	private final Object obj;
	private final Method method;

	public ReflectInvoker(Object obj, Method method) {
		this.obj = obj;
		this.method = method;
		ReflectionUtils.makeAccessible(method);
	}

	public Method getMethod() {
		return method;
	}

	@Override
	public Object getInstance() {
		return obj;
	}
}
