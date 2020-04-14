package scw.aop;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import scw.core.instance.InstanceUtils;

public final class ProxyUtils {
	private static final MultipleProxyAdapter PROXY_ADAPTER = new MultipleProxyAdapter();

	static {
		PROXY_ADAPTER.addAll(InstanceUtils
				.getSystemConfigurationList(ProxyAdapter.class));
	}

	private ProxyUtils() {
	};

	public static ProxyAdapter getProxyAdapter() {
		return PROXY_ADAPTER;
	}

	private static int ignoreHashCode(Object obj) {
		return System.identityHashCode(obj);
	}

	private static String ignoreToString(Object obj) {
		return obj.getClass().getName() + "@"
				+ Integer.toHexString(ignoreHashCode(obj));
	}

	/**
	 * 如果返回空说明此方法不能忽略
	 * 
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

		if (args != null && args.length == 1
				&& method.getName().equals("equals")) {
			return obj == args[0];
		}
		return null;
	}

	/**
	 * 代理一个实例
	 * 
	 * @param clazz
	 * @param instance
	 * @param interfaces
	 * @param filters
	 * @return
	 */
	public static Proxy proxyInstance(Class<?> clazz, Object instance,
			Class<?>[] interfaces, Collection<? extends Filter> filters) {
		return proxyInstance(clazz, instance, interfaces, filters, null);
	}

	/**
	 * 代理一个实例
	 * 
	 * @param clazz
	 * @param instance
	 * @param interfaces
	 * @param filters
	 * @param filterChain
	 * @return
	 */
	public static Proxy proxyInstance(Class<?> clazz, Object instance,
			Class<?>[] interfaces, Collection<? extends Filter> filters,
			FilterChain filterChain) {
		return getProxyAdapter().proxy(clazz, interfaces,
				Arrays.asList(new InstanceFilter(instance)),
				new DefaultFilterChain(filters, filterChain));
	}

	private static final class InstanceFilter implements Filter, Serializable {
		private static final long serialVersionUID = 1L;
		private final Object instnace;

		public InstanceFilter(Object instance) {
			this.instnace = instance;
		}

		public Object doFilter(Invoker invoker, Object proxy,
				Class<?> targetClass, Method method, Object[] args,
				FilterChain filterChain) throws Throwable {
			return filterChain.doFilter(instnace == null ? new EmptyInvoker(
					method) : new ReflectInvoker(instnace, method), proxy,
					targetClass, method, args);
		}
	}
}
