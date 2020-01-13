package scw.rpc.support;

import java.io.Serializable;
import java.lang.reflect.Method;

import scw.rpc.RequestMessage;
import scw.util.SerializableMethod;

public class ObjectRequestMessage extends RequestMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private SerializableMethod method;
	private Object[] parameters;

	public ObjectRequestMessage(Class<?> sourceClass, Method method, Object[] parameters) {
		this.method = new SerializableMethod(sourceClass, method);
		this.parameters = parameters;
	}

	@Override
	public Class<?> getSourceClass() {
		return method.getSourceClass();
	}

	@Override
	public Method getMethod() {
		return method.getMethod();
	}

	@Override
	public Object[] getParameters() {
		return parameters;
	}
}
