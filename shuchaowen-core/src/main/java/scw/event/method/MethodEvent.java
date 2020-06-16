package scw.event.method;

import java.io.Serializable;

import scw.aop.ProxyInvoker;
import scw.core.reflect.SerializableMethod;
import scw.event.support.SimpleEvent;

public class MethodEvent extends SimpleEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	private final SerializableMethod method;
	private final Object[] args;
	private final Object result;

	public MethodEvent(ProxyInvoker invoker, Object[] args) throws Throwable {
		this(new SerializableMethod(invoker.getTargetClass(), invoker.getMethod()), args,
				invoker.invoke(args));
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
