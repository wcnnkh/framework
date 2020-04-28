package scw.aop;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

import scw.core.instance.annotation.Configuration;
import scw.lang.NotSupportedException;
import scw.util.result.SimpleResult;

@Configuration(order = Integer.MIN_VALUE + 100)
public class JdkProxyAdapter extends AbstractProxyAdapter {

	public boolean isSupport(Class<?> clazz) {
		return clazz.isInterface();
	}

	public boolean isProxy(Class<?> clazz) {
		return java.lang.reflect.Proxy.isProxyClass(clazz);
	}

	public Class<?> getClass(Class<?> clazz, Class<?>[] interfaces) {
		return java.lang.reflect.Proxy.getProxyClass(clazz.getClassLoader(),
				mergeInterfaces(clazz, interfaces));
	}

	public Proxy proxy(Class<?> clazz, Class<?>[] interfaces,
			FilterChain filterChain) {
		return new JdkProxy(clazz, mergeInterfaces(clazz, interfaces),
				new FiltersInvocationHandler(clazz, filterChain));
	}

	private static final class FiltersInvocationHandler implements
			InvocationHandler, Serializable {
		private static final long serialVersionUID = 1L;
		private final Class<?> targetClass;
		private final FilterChain filterChain;

		public FiltersInvocationHandler(Class<?> targetClass,
				FilterChain filterChain) {
			this.targetClass = targetClass;
			this.filterChain = filterChain;
		}

		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			SimpleResult<Object> ignoreResult = ProxyUtils.ignoreMethod(proxy,
					method, args);
			if (ignoreResult.isSuccess()) {
				return ignoreResult.getData();
			}

			ProxyContext context = new ProxyContext(proxy, targetClass, method, args, null);
			Invoker invoker = new EmptyInvoker(method);
			return filterChain.doFilter(invoker, context);
		}
	}

	public Class<?> getUserClass(Class<?> clazz) {
		return clazz.getInterfaces()[0];
	}

	public static final class JdkProxy extends AbstractProxy {
		private Class<?>[] interfaces;
		private InvocationHandler invocationHandler;

		public JdkProxy(Class<?> clazz, Class<?>[] interfaces,
				InvocationHandler invocationHandler) {
			super(clazz);
			this.interfaces = interfaces;
			this.invocationHandler = invocationHandler;
		}

		public Object create() {
			return java.lang.reflect.Proxy.newProxyInstance(getTargetClass()
					.getClassLoader(), interfaces == null ? new Class<?>[0]
					: interfaces, invocationHandler);
		}

		public Object createInternal(Class<?>[] parameterTypes,
				Object[] arguments) {
			throw new NotSupportedException(getTargetClass().getName() + ","
					+ Arrays.toString(parameterTypes));
		}
	}
}
