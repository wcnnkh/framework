package scw.core.reflect;

import java.lang.reflect.Method;

public final class SimpleMethodHolder implements MethodHolder {
	private final Method method;
	private final Class<?> declaringClass;

	public SimpleMethodHolder(Class<?> declaringClass, Method method) {
		this.method = method;
		this.declaringClass = declaringClass;
	}

	public Method getMethod() {
		return method;
	}
	
	public Class<?> getDeclaringClass() {
		return declaringClass;
	}

	@Override
	public String toString() {
		return method.toString();
	}
}
