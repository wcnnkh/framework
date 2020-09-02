package scw.event.method;

import java.io.Serializable;

import scw.aop.MethodInvoker;
import scw.core.reflect.SerializableMethod;
import scw.event.support.BasicEvent;

public class MethodEvent extends BasicEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	private final SerializableMethod method;
	private final Object[] args;
	private final Object result;

	public MethodEvent(Object result, MethodInvoker invoker, Object[] args) throws Throwable {
		this(new SerializableMethod(invoker.getMethod()), args,
				result);
	}

	public MethodEvent(SerializableMethod method, Object[] args, Object result) {
		this.method = method;
		this.args = args;
		this.result = result;
	}

	public final SerializableMethod getMethod() {
		return method;
	}

	public final Object[] getArgs() {
		return args;
	}

	public final Object getResult() {
		return result;
	}
}