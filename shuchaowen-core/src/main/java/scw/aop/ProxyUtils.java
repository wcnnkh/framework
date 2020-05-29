package scw.aop;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import scw.core.GlobalPropertyFactory;
import scw.core.instance.InstanceUtils;
import scw.core.utils.ArrayUtils;

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
