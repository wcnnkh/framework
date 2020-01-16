package scw.core.reflect;

import java.lang.reflect.Method;

public interface MethodHolder {
	Object invoke(Object obj, Object... args) throws Throwable;

	Method getMethod();

	int getParameterCount();

	Class<?> getBelongClass();

	Class<?>[] getParameterTypes();

	String getMethodName();
}
