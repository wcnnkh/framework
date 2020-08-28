package scw.core.reflect;

import java.lang.reflect.Method;

public final class SimpleMethodHolder implements MethodHolder {
	private Method method;

	public SimpleMethodHolder(Method method) {
		this.method = method;
	}

	public Method getMethod() {
		return method;
	}

	@Override
	public String toString() {
		return method.toString();
	}
}
