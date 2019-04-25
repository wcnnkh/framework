package scw.aop.support;

import java.lang.reflect.Method;

import scw.aop.Invoker;

public final class ReflectInvoker implements Invoker {
	private final Object obj;
	private final Method method;

	public ReflectInvoker(Object obj, Method method) {
		this.obj = obj;
		this.method = method;
		method.setAccessible(true);
	}

	public Object invoke(Object... args) throws Throwable {
		return method.invoke(obj, args);
	}
	
	@Override
	public String toString() {
		return method.toString();
	}
}
