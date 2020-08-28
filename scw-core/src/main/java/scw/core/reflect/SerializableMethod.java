package scw.core.reflect;

import java.io.Serializable;
import java.lang.reflect.Method;

public final class SerializableMethod implements MethodHolder, Serializable {
	private static final long serialVersionUID = 1L;
	private volatile transient Method method;
	private final Class<?> declaringClass;
	private final String name;
	private final Class<?>[] parameterTypes;

	public SerializableMethod(Method method) {
		this.declaringClass = method == null ? null : method.getDeclaringClass();
		this.method = method;
		this.name = method == null ? null : method.getName();
		this.parameterTypes = method == null ? null : method.getParameterTypes();
	}

	public Method getMethod() {
		if (method == null) {
			synchronized (this) {
				if (method == null) {
					method = ReflectionUtils.findMethod(declaringClass, name, parameterTypes);
				}
			}
		}
		return method;
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
