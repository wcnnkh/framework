package scw.reflect;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class SerializableMethod implements Serializable, scw.reflect.Method {
	private static final long serialVersionUID = 1L;
	private Class<?> belongClass;
	private String methodName;
	private Class<?>[] parameterTypes;
	private transient Method method;

	public SerializableMethod(Class<?> belongClass, Method method) {
		this.belongClass = belongClass;
		this.methodName = method.getName();
		this.parameterTypes = method.getParameterTypes();
		this.method = method;
		if (!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(belongClass.getModifiers())) {
			method.setAccessible(true);
		}
	}

	public Method getMethod() throws NoSuchMethodException, SecurityException {
		if (method == null) {
			method = belongClass.getDeclaredMethod(methodName, parameterTypes);
			if (!Modifier.isPublic(method.getModifiers())) {
				method.setAccessible(true);
			}
		}
		return method;
	}

	public Object invoke(Object obj, Object... args) throws Throwable {
		return getMethod().invoke(obj, args);
	}

	public Class<?> getBelongClass() {
		return belongClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes.clone();
	}

	public int getParameterCount() {
		return parameterTypes.length;
	}
}
