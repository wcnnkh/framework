package scw.core.aop;

import java.lang.reflect.Proxy;
import java.util.Collection;

import scw.core.utils.Assert;

public final class ProxyUtils {
	private static final String JDK_PROXY_CLASS_NAME = "com.sun.proxy.$Proxy";

	private ProxyUtils() {
	};

	public static boolean isJDKProxy(Class<?> clazz) {
		return clazz.getName().startsWith(JDK_PROXY_CLASS_NAME);
	}

	public static boolean isJDKProxy(Object instance) {
		Assert.notNull(instance);
		return isJDKProxy(instance.getClass());
	}

	public static Object newProxyInstance(Object obj, Class<?> interfaceClass, Collection<Filter> filters) {
		return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				new FilterInvocationHandler(obj, filters));
	}

	public static Object newProxyInstance(Class<?> interfaceClass, Collection<Filter> filters, Invoker invoker) {
		return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				new InvokerFilterInvocationHandler(invoker, filters));
	}
}
