package scw.rpc.simple;

import java.io.Serializable;
import java.lang.reflect.Method;

import scw.aop.DefaultMethodInvoker;
import scw.core.reflect.SerializableMethod;
import scw.util.attribute.SimpleAttributes;

public class SimpleObjectRequestMessage extends SimpleAttributes<String, Object> implements Serializable {
	private static final long serialVersionUID = 1L;
	private SerializableMethod serializableMethod;
	private Object[] args;
	private Class<?> sourceClass;

	public SimpleObjectRequestMessage(Class<?> sourceClass, Method method, Object[] args) {
		this.serializableMethod = new SerializableMethod(method);
		this.args = args;
	}

	public SerializableMethod getSerializableMethod() {
		return serializableMethod;
	}

	public Class<?> getSourceClass() {
		return sourceClass == null ? serializableMethod.getDeclaringClass() : sourceClass;
	}

	public void setSourceClass(Class<?> sourceClass) {
		this.sourceClass = sourceClass;
	}

	public Object[] getArgs() {
		return args;
	}

	public Object invoke(Object instance) throws Throwable {
		return new DefaultMethodInvoker(instance, sourceClass, getSerializableMethod().getMethod()).invoke(getArgs());
	}

	@Override
	public String toString() {
		return getSerializableMethod().toString();
	}
}
