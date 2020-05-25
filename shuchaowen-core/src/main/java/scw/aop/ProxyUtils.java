package scw.aop;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import scw.core.GlobalPropertyFactory;
import scw.core.instance.InstanceUtils;
import scw.core.utils.ArrayUtils;
import scw.result.SimpleResult;

public final class ProxyUtils {

	private static final MultipleProxyFactory PROXY_FACTORY = new MultipleProxyFactory();

	public static final Collection<Class<Filter>> FILTERS = InstanceUtils
			.getConfigurationClassList(Filter.class,
					GlobalPropertyFactory.getInstance());

	static {
		PROXY_FACTORY.addAll(InstanceUtils
				.getSystemConfigurationList(ProxyFactory.class));
	}

	private ProxyUtils() {
	};

	public static MultipleProxyFactory getProxyFactory() {
		return PROXY_FACTORY;
	}

	private static int ignoreHashCode(Object obj) {
		return System.identityHashCode(obj);
	}

	private static String ignoreToString(Object obj) {
		return obj.getClass().getName() + "@"
				+ Integer.toHexString(ignoreHashCode(obj));
	}

	public static SimpleResult<Object> ignoreMethod(Object obj, Method method,
			Object[] args) {
		if (args == null || args.length == 0) {
			if (method.getName().equals("hashCode")) {
				return new SimpleResult<Object>(true, ignoreHashCode(obj));
			} else if (method.getName().equals("toString")) {
				return new SimpleResult<Object>(true, ignoreToString(obj));
			}
		}

		if (args != null && args.length == 1
				&& method.getName().equals("equals")) {
			return new SimpleResult<Object>(true, obj == args[0]);
		}
		return new SimpleResult<Object>(false, null);
	}

	/**
	 * 代理一个对象并忽略其指定的方法
	 * 
	 * @param clazz
	 * @param instance
	 * @param ignoreMethods
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T proxyIngoreMethod(Class<? extends T> clazz,
			T instance, IgnoreMethodAccept ignoreMethodAccept) {
		Proxy proxy = getProxyFactory().getProxy(
				clazz,
				new Class<?>[] { IgnoreMethodTarget.class },
				new DefaultFilterChain(Arrays.asList(new IgnoreMethodFilter(
						instance, ignoreMethodAccept))));
		return (T) proxy.create();
	}

	public static interface IgnoreMethodTarget {
		Object getIgnoreMethodTarget();
	}

	private static final class IgnoreMethodFilter implements Filter {
		private final Object object;
		private final IgnoreMethodAccept ignoreMethodAccept;

		public IgnoreMethodFilter(Object object,
				IgnoreMethodAccept ignoreMethodAccept) {
			this.object = object;
			this.ignoreMethodAccept = ignoreMethodAccept;
		}

		public Object doFilter(Invoker invoker, ProxyContext context,
				FilterChain filterChain) throws Throwable {
			if (ArrayUtils.isEmpty(context.getArgs())
					&& context.getMethod().equals("getIgnoreMethodTarget")) {
				return object;
			}

			if (ignoreMethodAccept != null
					&& ignoreMethodAccept.accept(context.getMethod())) {
				return null;
			}

			return filterChain.doFilter(invoker, context);
		}
	}

	public static interface IgnoreMethodAccept {
		boolean accept(Method method);
	}
}
