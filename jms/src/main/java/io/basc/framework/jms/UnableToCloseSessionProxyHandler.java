package io.basc.framework.jms;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.jms.Session;

public class UnableToCloseSessionProxyHandler implements InvocationHandler {
	private final Session target;

	public UnableToCloseSessionProxyHandler(Session target) {
		this.target = target;
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// Invocation on ConnectionProxy interface coming in...
		if (method.getName().equals("equals")) {
			// Only consider equal when proxies are identical.
			return (proxy == args[0]);
		} else if (method.getName().equals("hashCode")) {
			// Use hashCode of PersistenceManager proxy.
			return System.identityHashCode(proxy);
		} else if (method.getName().equals("close")) {
			return null;
		} else if (method.getName().equals("getTargetSession")) {
			return this.target;
		}

		// Invoke method on target Connection.
		try {
			return method.invoke(this.target, args);
		} catch (InvocationTargetException ex) {
			throw ex.getTargetException();
		}
	}
}
