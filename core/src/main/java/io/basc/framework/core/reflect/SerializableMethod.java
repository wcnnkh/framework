package io.basc.framework.core.reflect;

import java.io.Serializable;
import java.lang.reflect.Method;

import io.basc.framework.util.Assert;

public class SerializableMethod implements MethodHolder, Serializable {
	private static final long serialVersionUID = 1L;
	private volatile transient Method method;
	private final Class<?> declaringClass;
	private final String name;
	private final Class<?>[] parameterTypes;

	public SerializableMethod(Method method) {
		Assert.requiredArgument(method != null, "method");
		this.declaringClass = method.getDeclaringClass();
		this.method = method;
		this.name = method.getName();
		this.parameterTypes = method.getParameterTypes();
	}

	public Method getMethod() {
		if (method == null) {
			synchronized (this) {
				if (method == null) {
					method = ReflectionUtils.getDeclaredMethod(declaringClass, name, parameterTypes);
				}
			}
		}
		return method;
	}

	public Class<?> getSourceClass() {
		return declaringClass;
	}

	public Class<?> getDeclaringClass() {
		return declaringClass;
	}

	public String getName() {
		return name;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes.clone();
	}

	@Override
	public String toString() {
		return getMethod().toString();
	}
}
