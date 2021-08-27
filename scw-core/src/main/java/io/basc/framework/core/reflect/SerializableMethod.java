package io.basc.framework.core.reflect;

import java.io.Serializable;
import java.lang.reflect.Method;

public class SerializableMethod implements MethodHolder, Serializable {
	private static final long serialVersionUID = 1L;
	private volatile transient Method method;
	private final Class<?> declaringClass;
	private final String name;
	private final Class<?>[] parameterTypes;
	
	public SerializableMethod(Method method){
		this(method.getDeclaringClass(), method);
	}

	public SerializableMethod(Class<?> declaringClass, Method method) {
		this.declaringClass = declaringClass;
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
