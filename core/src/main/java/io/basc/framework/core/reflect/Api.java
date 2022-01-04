package io.basc.framework.core.reflect;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Api<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private final Class<T> declaringClass;
	private final T instance;

	public Api(Class<T> declaringClass, T instance) {
		this.declaringClass = declaringClass;
		this.instance = instance;
	}

	public Class<T> getDeclaringClass() {
		return declaringClass;
	}

	public T getInstance() {
		return instance;
	}

	public Object invoke(Method method, Object... args) {
		ReflectionUtils.makeAccessible(method);
		return ReflectionUtils.invokeMethod(method, Modifier.isStatic(method.getModifiers()) ? null : instance, args);
	}
}
