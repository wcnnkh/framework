package scw.rpc.support;

import java.io.Serializable;
import java.lang.reflect.Type;

import scw.rpc.ResponseMessage;

public class ObjectResponseMessage implements ResponseMessage, Serializable {
	private static final long serialVersionUID = 1L;
	private Object value;
	private Throwable error;
	private Class<?> valueType;
	private Type valueGenericType;

	public ObjectResponseMessage(Object value, Throwable error, Class<?> valueType, Type valueGenericType) {
		this.value = value;
		this.error = error;
		this.valueType = valueType;
		this.valueGenericType = valueGenericType;
	}

	public Object getValue() {
		return value;
	}

	public Throwable getError() {
		return error;
	}

	public Class<?> getValueType() {
		return valueType;
	}

	public Type getValueGenericType() {
		return valueGenericType;
	}

}
