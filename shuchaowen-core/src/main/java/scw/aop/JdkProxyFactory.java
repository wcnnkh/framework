package scw.aop;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

import scw.core.instance.annotation.Configuration;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.lang.NotSupportedException;

@Configuration(order = Integer.MIN_VALUE + 100)
public class JdkProxyFactory implements ProxyFactory {

	public boolean isSupport(Class<?> clazz) {
		return clazz.isInterface();
	}

	public boolean isProxy(Class<?> clazz) {
		return java.lang.reflect.Proxy.isProxyClass(clazz);
	}

	protected final Class<?>[] mergeInterfaces(Class<?> clazz, Class<?>[] interfaces) {
		if (ArrayUtils.isEmpty(interfaces)) {
			if (clazz.isInterface()) {
				return new Class<?>[] { clazz };
			} else {
				return new Class<?>[0];
			}
		} else {
			Class<?>[] array = new Class<?>[1 + interfaces.length];
			int index = 0;
			array[index++] = clazz;
			for (int i = 0; i < interfaces.length; i++) {
				if (interfaces[i] == clazz) {
					continue;
				}

				array[index++] = interfaces[i];
			}

			if (index <= interfaces.length) {
				return Arrays.copyOfRange(array, 0, index);
			} else {
				return array;
			}
		}
	}

	public Class<?> getProxyClass(Class<?> clazz, Class<?>[] interfaces) {
		return java.lang.reflect.Proxy.getProxyClass(clazz.getClassLoader(), mergeInterfaces(clazz, interfaces));
	}

	public Proxy getProxy(Class<?> clazz, Class<?>[] interfaces, Iterable<? extends MethodInterceptor> filters) {
		return new JdkProxy(clazz, mergeInterfaces(clazz, interfaces), new FiltersInvocationHandler(clazz, filters));
	}

	private static final class FiltersInvocationHandler implements InvocationHandler, Serializable {
		private static final long serialVersionUID = 1L;
		private final Class<?> targetClass;
		private final Iterable<? extends MethodInterceptor> filters;

		public FiltersInvocationHandler(Class<?> targetClass, Iterable<? extends MethodInterceptor> filters) {
			this.targetClass = targetClass;
			this.filters = filters;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			JdkProxyInvoker invoker = new JdkProxyInvoker(proxy, targetClass, method);
			if (filters == null) {
				return invoker.invoke(args);
			}
			return new MethodInterceptorChain(filters.iterator()).intercept(invoker, args);
		}
	}

	private static class JdkProxyInvoker extends DefaultMethodInvoker {
		private static final long serialVersionUID = 1L;

		JdkProxyInvoker(Object proxy, Class<?> targetClass, Method method) {
			super(proxy, targetClass, method);
		}

		public Object invoke(Object... args) throws Throwable {
			// 如果filter中没有拦截这些方法，那么使用默认的调用
			if (ProxyUtils.isIgnoreMethod(getMethod())) {
				return ProxyUtils.invokeIgnoreMethod(this, args);
			}

			throw new UnsupportedOperationException(toString());
		}
	}

	public Class<?> getUserClass(Class<?> clazz) {
		return clazz.getInterfaces()[0];
	}

	public static final class JdkProxy extends AbstractProxy {
		private Class<?>[] interfaces;
		private InvocationHandler invocationHandler;

		public JdkProxy(Class<?> clazz, Class<?>[] interfaces, InvocationHandler invocationHandler) {
			super(clazz);
			this.interfaces = interfaces;
			this.invocationHandler = invocationHandler;
		}

		public Object create() {
			return java.lang.reflect.Proxy.newProxyInstance(getTargetClass().getClassLoader(),
					interfaces == null ? new Class<?>[0] : interfaces, invocationHandler);
		}

		public Object createInternal(Class<?>[] parameterTypes, Object[] arguments) {
			throw new NotSupportedException(getTargetClass().getName() + "," + Arrays.toString(parameterTypes));
		}
	}

	private static final String PROXY_NAME_PREFIX = "java.lang.reflect.Proxy";

	public boolean isProxy(String className) {
		return className.startsWith(PROXY_NAME_PREFIX);
	}

	public boolean isProxy(String className, ClassLoader classLoader) {
		return className.startsWith(PROXY_NAME_PREFIX);
	}

	public Class<?> getUserClass(String className, boolean initialize, ClassLoader classLoader)
			throws ClassNotFoundException {
		return getUserClass(ClassUtils.forName(className, initialize, classLoader));
	}
}
