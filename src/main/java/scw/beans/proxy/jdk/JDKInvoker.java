package scw.beans.proxy.jdk;

import java.lang.reflect.Method;

import scw.beans.proxy.Invoker;

public final class JDKInvoker implements Invoker {
	private final Object obj;
	private final Method method;
	private final Object[] args;

	public JDKInvoker(Object obj, Method method, Object[] args) {
		this.obj = obj;
		this.method = method;
		this.args = args;
	}

	public Object invoke() throws Throwable {
		return method.invoke(obj, args);
	}

}
