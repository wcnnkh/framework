package scw.jdbc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;

public class ConnectionProxyHandler implements InvocationHandler {
	private final Connection target;

	public ConnectionProxyHandler(Connection target) {
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
		} else if (method.getName().equals("unwrap")) {
			if (((Class<?>) args[0]).isInstance(proxy)) {
				return proxy;
			}
		} else if (method.getName().equals("isWrapperFor")) {
			if (((Class<?>) args[0]).isInstance(proxy)) {
				return true;
			}
		} else if (method.getName().equals("close")) {
			// Handle close method: suppress, not valid.
			return null;
		} else if (method.getName().equals("isClosed")) {
			return false;
		} else if (method.getName().equals("getTargetConnection")) {
			// Handle getTargetConnection method: return underlying
			// Connection.
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
