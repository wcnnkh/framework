package scw.core.reflect;

import java.lang.reflect.Method;

public abstract class AbstractMethodHolder implements MethodHolder {

	public Class<?>[] getParameterTypes() {
		Method method = getMethod();
		if (method == null) {
			return new Class<?>[0];
		}
		return method.getParameterTypes();
	}

	public int getParameterCount() {
		return getParameterTypes().length;
	}

	public Object invoke(Object obj, Object... args) throws Throwable {
		return getMethod().invoke(obj, args);
	}

	@Override
	public String toString() {
		return getMethod().toString();
	}
}
