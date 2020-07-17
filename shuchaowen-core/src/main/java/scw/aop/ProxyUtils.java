package scw.aop;

import java.io.Serializable;
import java.lang.reflect.Method;

import scw.compatible.CompatibleUtils;
import scw.compatible.ServiceLoader;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;

public final class ProxyUtils {
	private static final MultipleProxyFactory PROXY_FACTORY = new MultipleProxyFactory();

	static {
		ServiceLoader<ProxyFactory> serviceLoader = CompatibleUtils.getSpi().load(ProxyFactory.class);
		for (ProxyFactory proxyFactory : serviceLoader) {
			PROXY_FACTORY.add(proxyFactory);
		}
		PROXY_FACTORY.add(new CglibProxyFactory());
		PROXY_FACTORY.add(new JdkProxyFactory());
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
	public static <T> T proxyIngoreMethod(Class<? extends T> clazz, T instance, IgnoreMethodAccept ignoreMethodAccept) {
		Proxy proxy = getProxyFactory().getProxy(clazz, new Class<?>[] { IgnoreMethodTarget.class },
				new IgnoreMethodFilter(instance, ignoreMethodAccept));
		return (T) proxy.create();
	}

	public static interface IgnoreMethodTarget {
		Object getIgnoreMethodTarget();
	}

	private static final class IgnoreMethodFilter implements Filter {
		private final Object object;
		private final IgnoreMethodAccept ignoreMethodAccept;

		public IgnoreMethodFilter(Object object, IgnoreMethodAccept ignoreMethodAccept) {
			this.object = object;
			this.ignoreMethodAccept = ignoreMethodAccept;
		}

		public Object doFilter(ProxyInvoker invoker, Object[] args) throws Throwable {
			if (ArrayUtils.isEmpty(args) && invoker.getMethod().equals("getIgnoreMethodTarget")) {
				return object;
			}

			if (ignoreMethodAccept != null && ignoreMethodAccept.accept(invoker.getMethod())) {
				return null;
			}

			return invoker.invoke(args);
		}
	}

	public static interface IgnoreMethodAccept {
		boolean accept(Method method);
	}

	public static boolean isIgnoreMethod(Method method) {
		return ReflectionUtils.isHashCodeMethod(method) && ReflectionUtils.isToStringMethod(method)
				&& ReflectionUtils.isEqualsMethod(method);
	}

	public static int invokeHashCode(ProxyInvoker invoker) {
		return System.identityHashCode(invoker.getProxy());
	}

	public static String invokeToString(ProxyInvoker invoker) {
		return invoker.getProxy().getClass().getName() + "@" + Integer.toHexString(invokeHashCode(invoker));
	}

	public static boolean invokeEquals(ProxyInvoker invoker, Object[] args) {
		if (args == null || args[0] == null) {
			return false;
		}

		return args[0].equals(invoker.getProxy());
	}

	public static Object invokeIgnoreMethod(ProxyInvoker invoker, Object[] args) {
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
	public static boolean isWriteReplaceMethod(ProxyInvoker invoker) {
		return ArrayUtils.isEmpty(invoker.getMethod().getParameterTypes()) && invoker.getProxy() instanceof Serializable
				&& invoker.getMethod().getName().equals(WriteReplaceInterface.WRITE_REPLACE_METHOD);
	}

	/**
	 * 是否是ObjectStream中的WriteReplaceMethod
	 * 
	 * @param writeReplaceInterface
	 *            原始类型是否应该实现{@see WriteReplaceInterface}
	 * @return
	 */
	public static boolean isWriteReplaceMethod(ProxyInvoker invoker, boolean writeReplaceInterface) {
		if (isWriteReplaceMethod(invoker)) {
			if (writeReplaceInterface) {
				return WriteReplaceInterface.class.isAssignableFrom(invoker.getTargetClass());
			} else {
				return !WriteReplaceInterface.class.isAssignableFrom(invoker.getTargetClass());
			}
		}
		return false;
	}
}
