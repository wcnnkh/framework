package scw.aop.support;

import java.lang.reflect.Proxy;
import java.util.Collection;

import scw.aop.Filter;
import scw.aop.Invoker;

public final class JDKProxyUtils {
	private JDKProxyUtils() {
	};

	public static Object newProxyInstance(Object obj, Class<?> interfaceClass, Collection<Filter> filters) {
		return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				new FilterInvocationHandler(obj, filters));
	}

	public static Object newProxyInstance(Class<?> interfaceClass, Collection<Filter> filters, Invoker invoker) {
		return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				new InvokerFilterInvocationHandler(invoker, filters));
	}
}
