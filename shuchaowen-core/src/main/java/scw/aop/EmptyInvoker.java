package scw.aop;

import java.lang.reflect.Method;

import scw.lang.UnsupportedException;

public class EmptyInvoker implements Invoker {
	private final Method method;

	public EmptyInvoker(Method method) {
		this.method = method;
	}

	public Object invoke(Object... args) throws Throwable {
		throw new UnsupportedException(method.toString());
	}
}
