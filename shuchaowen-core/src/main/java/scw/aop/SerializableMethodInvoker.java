package scw.aop;

import java.io.Serializable;
import java.lang.reflect.Method;

import scw.core.reflect.SerializableMethod;

public abstract class SerializableMethodInvoker extends MethodInvoker implements
		Serializable {
	private static final long serialVersionUID = 1L;
	private final SerializableMethod serializableMethod;

	public SerializableMethodInvoker(SerializableMethod serializableMethod) {
		this.serializableMethod = serializableMethod;
	}

	@Override
	public Method getMethod() {
		return serializableMethod.getMethod();
	}
}
