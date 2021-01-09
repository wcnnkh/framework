package scw.util;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import scw.core.Assert;

public class SynchronizedInvocationHandler implements InvocationHandler,
		Serializable {
	private static final long serialVersionUID = 1L;
	private final Object mutex; // Object on which to synchronize
	private final Object source;

	public SynchronizedInvocationHandler(Object source, Object mutex) {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(mutex != null, "mutex");
		this.mutex = mutex;
		this.source = source;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		synchronized (mutex) {
			return method.invoke(source, args);
		}
	}
}
