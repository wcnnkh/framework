package scw.core.reflect;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class SerializableMethodDefinition implements Serializable, MethodDefinition {
	private static final long serialVersionUID = 1L;
	private Class<?> belongClass;
	private String methodName;
	private Class<?>[] parameterTypes;

	private volatile transient Method method;

	// 用于序列化
	protected SerializableMethodDefinition() {
	};

	public SerializableMethodDefinition(Class<?> belongClass, Method method) {
		this.belongClass = belongClass;
		this.methodName = method.getName();
		this.parameterTypes = method.getParameterTypes();
		this.method = method;
		if (!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(belongClass.getModifiers())) {
			method.setAccessible(true);
		}
	}

	public Method getMethod() {
		if (method == null) {
			synchronized (this) {
				if (method == null) {
					this.method = ReflectUtils.getMethod(belongClass, methodName, parameterTypes);
				}
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
