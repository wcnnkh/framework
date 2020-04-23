package scw.net.rpc.simple;

import java.io.Serializable;
import java.lang.reflect.Method;

import scw.core.reflect.MethodHolder;
import scw.core.reflect.SerializableMethodHolder;
import scw.util.attribute.SimpleAttributes;

public class SimpleObjectRequestMessage extends SimpleAttributes<String, Object> implements Serializable {
	private static final long serialVersionUID = 1L;
	private MethodHolder methodHolder;
	private Object[] args;

	public SimpleObjectRequestMessage(Class<?> clz, Method method, Object[] args) {
		this.methodHolder = new SerializableMethodHolder(clz, method);
		this.args = args;
	}

	public MethodHolder getMethodHolder() {
		return methodHolder;
	}

	public Object[] getArgs() {
		return args;
	}

	public Object invoke(Object instance) throws Throwable {
		return getMethodHolder().invoke(instance, args);
	}
	
	@Override
	public String toString() {
		return methodHolder.toString();
	}
}
