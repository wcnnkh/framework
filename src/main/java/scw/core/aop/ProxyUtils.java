package scw.core.aop;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collection;

import scw.core.Assert;
import scw.core.cglib.proxy.Enhancer;
import scw.core.utils.ClassUtils;

public final class ProxyUtils {
	private static final String JDK_PROXY_CLASS_NAME = "com.sun.proxy.$Proxy";

	private ProxyUtils() {
	};

	public static boolean isJDKProxy(Class<?> clazz) {
		return clazz.getName().startsWith(JDK_PROXY_CLASS_NAME);
	}

	public static boolean isProxy(Object obj) {
		if (obj == null) {
			return false;
		}

		if (ClassUtils.isCglibProxy(obj)) {
			return true;
		}

		return isJDKProxy(obj);
	}

	public static boolean isJDKProxy(Object instance) {
		Assert.notNull(instance);
		return isJDKProxy(instance.getClass());
	}

	public static Object proxyInstance(Object obj, Class<?> interfaceClass, Filter... filters) {
		return proxyInstance(obj, interfaceClass, Arrays.asList(filters));
	}

	public static Object proxyInstance(Object obj, Class<?> interfaceClass, Collection<Filter> filters) {
		return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				new FilterInvocationHandler(obj, filters));
	}

	public static Object newProxyInstance(Invoker invoker, Class<?> interfaceClass, Filter... filters) {
		return newProxyInstance(invoker, interfaceClass, Arrays.asList(filters));
	}

	public static Object newProxyInstance(Invoker invoker, Class<?> interfaceClass, Collection<Filter> filters) {
		return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				new InvokerFilterInvocationHandler(invoker, filters));
	}

	public static Enhancer createEnhancer(Class<?> type, Filter... filters) {
		return createEnhancer(type, Arrays.asList(filters));
	}

	public static Enhancer createEnhancer(Class<?> type, Collection<Filter> filters) {
		Enhancer enhancer = new Enhancer();
		enhancer.setCallback(new FiltersConvertCglibMethodInterceptor(filters));
		if (Serializable.class.isAssignableFrom(type)) {
			enhancer.setSerialVersionUID(1L);
		}
		enhancer.setSuperclass(type);
		return enhancer;
	}

	private static int ignoreHashCode(Object obj) {
		return System.identityHashCode(obj);
	}

	private static String ignoreToString(Object obj) {
		return obj.getClass().getName() + "@" + Integer.toHexString(ignoreHashCode(obj));
	}

	/**
	 * 如果返回空说明此方法不能忽略
	 * @param obj
	 * @param method
	 * @param args
	 * @return
	 */
	public static Object ignoreMethod(Object obj, Method method, Object[] args) {
		if (args == null || args.length == 0) {
			if (method.getName().equals("hashCode")) {
				return ignoreHashCode(obj);
			} else if (method.getName().equals("toString")) {
				return ignoreToString(obj);
			}
		}

		if (args != null && args.length == 1 && method.getName().equals("equals")) {
			return obj == args[0];
		}

		return null;
	}
}
