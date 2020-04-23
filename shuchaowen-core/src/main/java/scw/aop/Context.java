package scw.aop;

import java.lang.reflect.Method;

import scw.util.attribute.SimpleAttributes;

public class Context extends SimpleAttributes<String, Object> {
	private static final long serialVersionUID = 1L;
	private final Object proxy;
	private final Class<?> targetClass;
	private final Method method;
	private final Object[] args;

	public Context(Object proxy, Class<?> targetClass, Method method, Object[] args) {
		this.proxy = proxy;
		this.targetClass = targetClass;
		this.method = method;
		this.args = args;
	}

	public Object getProxy() {
		return proxy;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public Method getMethod() {
		return method;
	}

	public Object[] getArgs() {
		return args;
	}
	
	@Override
	public String toString() {
		return method.toString();
	}
}
