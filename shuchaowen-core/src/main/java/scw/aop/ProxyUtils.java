package scw.aop;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;

public final class ProxyUtils {
	private static final ProxyFactory PROXY_FACTORY;

	static {
		List<ProxyFactory> proxyFactories = new ArrayList<ProxyFactory>();
		proxyFactories.addAll(InstanceUtils.loadAllService(ProxyFactory.class));
		proxyFactories.add(new JdkProxyFactory());
		proxyFactories.add(new CglibProxyFactory());
		PROXY_FACTORY = new MultiProxyFactory(Arrays.asList(proxyFactories.toArray(new ProxyFactory[0])));
	}

	private ProxyUtils() {
	};

	public static ProxyFactory getProxyFactory() {
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
	public static <T> T proxyIngoreMethod(Class<? extends T> clazz, T instance, IgnoreMethodAccept ignoreMethodAccept) {
		Proxy proxy = getProxyFactory().getProxy(clazz, new Class<?>[] { IgnoreMethodTarget.class },
				Arrays.asList(new IgnoreMethodFilter(instance, ignoreMethodAccept)));
		return (T) proxy.create();
	}

	public static interface IgnoreMethodTarget {
		Object getIgnoreMethodTarget();
	}

	private static final class IgnoreMethodFilter implements MethodInterceptor {
		private final Object object;
		private final IgnoreMethodAccept ignoreMethodAccept;

		public IgnoreMethodFilter(Object object, IgnoreMethodAccept ignoreMethodAccept) {
			this.object = object;
			this.ignoreMethodAccept = ignoreMethodAccept;
		}

		public Object intercept(MethodInvoker invoker, Object[] args, MethodInterceptorChain filterChain) throws Throwable {
			if (ArrayUtils.isEmpty(args) && invoker.getMethod().getName().equals("getIgnoreMethodTarget")) {
				return object;
			}

			if (ignoreMethodAccept != null && ignoreMethodAccept.accept(invoker.getMethod())) {
				return null;
			}

			return filterChain.intercept(invoker, args);
		}
	}

	public static interface IgnoreMethodAccept {
		boolean accept(Method method);
	}

	public static boolean isIgnoreMethod(Method method) {
		return ReflectionUtils.isHashCodeMethod(method) && ReflectionUtils.isToStringMethod(method)
				&& ReflectionUtils.isEqualsMethod(method);
	}

	public static int invokeHashCode(MethodInvoker invoker) {
		return System.identityHashCode(invoker.getInstance());
	}

	public static String invokeToString(MethodInvoker invoker) {
		return invoker.getInstance().getClass().getName() + "@" + Integer.toHexString(invokeHashCode(invoker));
	}

	public static boolean invokeEquals(MethodInvoker invoker, Object[] args) {
		if (args == null || args[0] == null) {
			return false;
		}

		return args[0].equals(invoker.getInstance());
	}

	public static Object invokeIgnoreMethod(MethodInvoker invoker, Object[] args) {
		if (ReflectionUtils.isHashCodeMethod(invoker.getMethod())) {
			return invokeHashCode(invoker);
		}

		if (ReflectionUtils.isToStringMethod(invoker.getMethod())) {
			return invokeToString(invoker);
		}

		if (ReflectionUtils.isEqualsMethod(invoker.getMethod())) {
			return invokeEquals(invoker, args);
		}

		throw new UnsupportedOperationException(invoker.getMethod().toString());
	}

	/**
	 * 是否是ObjectStream中的WriteReplaceMethod
	 * 
	 * @return
	 */
	public static boolean isWriteReplaceMethod(MethodInvoker invoker) {
		return ArrayUtils.isEmpty(invoker.getMethod().getParameterTypes()) && invoker.getInstance() instanceof Serializable
				&& invoker.getMethod().getName().equals(WriteReplaceInterface.WRITE_REPLACE_METHOD);
	}

	/**
	 * 是否是ObjectStream中的WriteReplaceMethod
	 * 
	 * @param writeReplaceInterface
	 *            原始类型是否应该实现{@see WriteReplaceInterface}
	 * @return
	 */
	public static boolean isWriteReplaceMethod(MethodInvoker invoker, boolean writeReplaceInterface) {
		if (isWriteReplaceMethod(invoker)) {
			if (writeReplaceInterface) {
				return WriteReplaceInterface.class.isAssignableFrom(invoker.getSourceClass());
			} else {
				return !WriteReplaceInterface.class.isAssignableFrom(invoker.getSourceClass());
			}
		}
		return false;
	}

	public static MethodInvoker wrapper(MethodInvoker invoker, Iterable<? extends MethodInterceptor> filters) {
		return new FilterProxyInvoker(invoker, filters);
	}

	private static class FilterProxyInvoker extends MethodInvokerWrapper implements Serializable {
		private static final long serialVersionUID = 1L;
		private Iterable<? extends MethodInterceptor> filters;

		public FilterProxyInvoker(MethodInvoker invoker, Iterable<? extends MethodInterceptor> filters) {
			super(invoker);
			this.filters = filters;
		}

		@Override
		public Object invoke(Object... args) throws Throwable {
			if (filters == null) {
				return super.invoke(args);
			}

			return new MethodInterceptorChain(filters.iterator()).intercept(getSource(), args);
		}
	}
}
