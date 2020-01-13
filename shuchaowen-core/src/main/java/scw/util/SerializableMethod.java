package scw.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;

import scw.core.reflect.ReflectionUtils;

public class SerializableMethod implements Serializable {
	private static final long serialVersionUID = 1L;
	private Class<?> sourceClass;
	private Class<?>[] parameterTypes;
	private String methodName;

	public SerializableMethod(Class<?> sourceClass, Method method) {
		this.sourceClass = sourceClass;
		this.parameterTypes = method.getParameterTypes();
		this.methodName = method.getName();
	}
	
	public Class<?> getSourceClass() {
		return sourceClass;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public String getMethodName() {
		return methodName;
	}

	private transient Method method;

	public Method getMethod() {
		if (this.method == null) {
			synchronized (this) {
				this.method = getMethodInternal();
			}
		}
		return this.method;
	}

	private Method getMethodInternal() {
		return ReflectionUtils.findMethod(sourceClass, methodName, parameterTypes);
	}

	// 在进行反序列化时使用反射获取方法
	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
		this.method = getMethodInternal();
	}
}
