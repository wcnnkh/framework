package scw.aop.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

import scw.aop.AbsttractProxyAdapter;
import scw.aop.DefaultFilterChain;
import scw.aop.EmptyInvoker;
import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Proxy;
import scw.aop.ProxyUtils;

public class JdkProxyAdapter extends AbsttractProxyAdapter {

	public boolean isSupport(Class<?> clazz) {
		return clazz.isInterface();
	}

	public boolean isProxy(Class<?> clazz) {
		return java.lang.reflect.Proxy.isProxyClass(clazz);
	}

	public Class<?> getClass(Class<?> clazz, Class<?>[] interfaces) {
		return java.lang.reflect.Proxy.getProxyClass(clazz.getClassLoader(), getInterfaces(clazz, interfaces));
	}

	public Proxy proxy(Class<?> clazz, Class<?>[] interfaces, Collection<? extends Filter> filters,
			FilterChain filterChain) {
		return new JdkProxy(clazz, getInterfaces(clazz, interfaces),
				new FiltersInvocationHandler(clazz, filters, filterChain));
	}

	private static final class FiltersInvocationHandler implements InvocationHandler {
		private final Collection<? extends Filter> filters;
		private final Class<?> targetClass;
		private final FilterChain filterChain;

		public FiltersInvocationHandler(Class<?> targetClass, Collection<? extends Filter> filters,
				FilterChain filterChain) {
			this.targetClass = targetClass;
			this.filters = filters;
			this.filterChain = filterChain;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object ignoreReturn = ProxyUtils.ignoreMethod(proxy, method, args);
			if (ignoreReturn != null) {
				return ignoreReturn;
			}

			FilterChain filterChain = new DefaultFilterChain(filters, this.filterChain);
			return filterChain.doFilter(new EmptyInvoker(method), proxy, targetClass, method, args);
		}
	}
}
