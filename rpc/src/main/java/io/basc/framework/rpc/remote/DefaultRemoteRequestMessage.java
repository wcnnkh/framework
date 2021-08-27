package io.basc.framework.rpc.remote;

import io.basc.framework.core.reflect.ReflectionUtils;

import java.lang.reflect.Method;

public class DefaultRemoteRequestMessage extends RemoteRequestMessage{
	private static final long serialVersionUID = 1L;
	private final Class<?> targetClass;
	private final String methodName;
	private final Class<?>[] parameterTypes;
	private final Object[] args;

	public DefaultRemoteRequestMessage(Class<?> targetClass,
			Method method, Object[] args) {
		this.targetClass = targetClass;
		this.methodName = method.getName();
		this.parameterTypes = method.getParameterTypes();
		this.args = args;
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

	public Object[] getArgs() {
		return args;
	}

	public Method getMethod() {
		return ReflectionUtils.findMethod(targetClass, methodName,
				parameterTypes);
	}

	@Override
	public String toString() {
		return getMethod().toString();
	}
}
