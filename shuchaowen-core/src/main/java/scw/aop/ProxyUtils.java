package scw.aop;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import scw.core.instance.InstanceUtils;
import scw.util.result.CommonResult;

public final class ProxyUtils {

	private static final MultipleProxyAdapter PROXY_ADAPTER = new MultipleProxyAdapter();

	static {
		PROXY_ADAPTER.addAll(InstanceUtils.getSystemConfigurationList(ProxyAdapter.class));
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
		return obj.getClass().getName() + "@" + Integer.toHexString(ignoreHashCode(obj));
	}

	/**
	 * 如果返回空说明此方法不能忽略
	 * 
	 * @param obj
	 * @param method
	 * @param args
	 * @return
	 */
	public static CommonResult<Object> ignoreMethod(Object obj, Method method, Object[] args) {
		if (args == null || args.length == 0) {
			if (method.getName().equals("hashCode")) {
				return new CommonResult<Object>(true, ignoreHashCode(obj));
			} else if (method.getName().equals("toString")) {
				return new CommonResult<Object>(true, ignoreToString(obj));
			}
		}

		if (args != null && args.length == 1 && method.getName().equals("equals")) {
			return new CommonResult<Object>(true, obj == args[0]);
		}
		return new CommonResult<Object>(false);
	}

	/**
	 * 代理一个实例
	 * 
	 * @param proxyAdapter
	 * @param instance
	 * @param clazz
	 * @param interfaces
	 * @param filters
	 * @param filterChain
	 * @return
	 */
	public static <T> Proxy proxy(ProxyAdapter proxyAdapter, T instance, Class<? extends T> clazz,
			Class<?>[] interfaces, Collection<? extends Filter> filters, FilterChain filterChain) {
		return proxyAdapter.proxy(clazz, interfaces, Arrays.asList(new InstanceFilter(instance)),
				new DefaultFilterChain(filters, filterChain));
	}

	/**
	 * 代理一个实例
	 * 
	 * @param proxyAdapter
	 * @param instance
	 * @param clazz
	 * @param interfaces
	 * @param filters
	 * @return
	 */
	public static <T> Proxy proxy(ProxyAdapter proxyAdapter, T instance, Class<? extends T> clazz,
			Class<?>[] interfaces, Collection<? extends Filter> filters) {
		return proxy(proxyAdapter, instance, clazz, interfaces, filters, null);
	}

	private static final class InstanceFilter implements Filter, Serializable {
		private static final long serialVersionUID = 1L;
		private final Object instnace;

		public InstanceFilter(Object instance) {
			this.instnace = instance;
		}

		public Object doFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args,
				FilterChain filterChain) throws Throwable {
			return filterChain.doFilter(
					instnace == null ? new EmptyInvoker(method) : new ReflectInvoker(instnace, method), proxy,
					targetClass, method, args);
		}
	}
}
