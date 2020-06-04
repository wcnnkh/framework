package scw.rpc.simple;

import java.io.Serializable;
import java.lang.reflect.Method;

import scw.aop.ReflectInvoker;
import scw.core.reflect.SerializableMethod;
import scw.util.attribute.SimpleAttributes;

public class SimpleObjectRequestMessage extends SimpleAttributes<String, Object> implements Serializable {
	private static final long serialVersionUID = 1L;
	private SerializableMethod serializableMethod;
	private Object[] args;

	public SimpleObjectRequestMessage(Class<?> clz, Method method, Object[] args) {
		this.serializableMethod = new SerializableMethod(clz, method);
		this.args = args;
	}

	public SerializableMethod getSerializableMethod() {
		return serializableMethod;
	}

	public Object[] getArgs() {
		return args;
	}

	public Object invoke(Object instance) throws Throwable {
		return new ReflectInvoker(instance, getSerializableMethod().getMethod()).invoke(getArgs());
	}
	
	@Override
	public String toString() {
		return getSerializableMethod().toString();
	}
}
