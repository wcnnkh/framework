package scw.core.reflect;

import java.io.Serializable;
import java.lang.reflect.Method;

public class SerializableMethod implements Serializable {
	private static final long serialVersionUID = 1L;
	private volatile transient Method method;
	private final Class<?> targetClass;
	private final String methodName;
	private final Class<?>[] parameterTypes;

	public SerializableMethod(Class<?> targetClass, Method method) {
		this.targetClass = targetClass;
		this.method = method;
		this.methodName = method == null ? null : method.getName();
		this.parameterTypes = method == null ? null : method
				.getParameterTypes();
	}

	public Method getMethod() {
		if (method == null) {
			synchronized (this) {
				if (method == null) {
					method = ReflectionUtils.findMethod(targetClass,
							methodName, parameterTypes);
				}
			}
		}
		return method;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
	
	@Override
	public String toString() {
		return getMethod().toString();
	}
}
