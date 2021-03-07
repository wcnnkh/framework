package scw.rpc.messageing.support;

import java.lang.reflect.Method;

import scw.core.reflect.ReflectionUtils;
import scw.rpc.messageing.BasicRemoteRequestMessage;
import scw.rpc.messageing.RemoteMethodRequestMessage;

public class DefaultRemoteMethodRequestMessage extends BasicRemoteRequestMessage implements RemoteMethodRequestMessage{
	private static final long serialVersionUID = 1L;
	private final Class<?> targetClass;
	private final String methodName;
	private final Class<?>[] parameterTypes;
	private final Object[] args;

	public DefaultRemoteMethodRequestMessage(Class<?> targetClass,
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
