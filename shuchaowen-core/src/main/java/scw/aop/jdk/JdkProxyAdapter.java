package scw.aop.jdk;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;

import scw.aop.AbstractProxyAdapter;
import scw.aop.DefaultFilterChain;
import scw.aop.EmptyInvoker;
import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Proxy;
import scw.aop.ProxyUtils;
import scw.core.instance.annotation.Configuration;
import scw.util.result.CommonResult;

@Configuration(order = Integer.MIN_VALUE + 100)
public class JdkProxyAdapter extends AbstractProxyAdapter {

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

	private static final class FiltersInvocationHandler implements InvocationHandler, Serializable {
		private static final long serialVersionUID = 1L;
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
			CommonResult<Object> ignoreResult = ProxyUtils.ignoreMethod(proxy, method, args);
			if (ignoreResult.isSuccess()) {
				return ignoreResult.getData();
			}

			FilterChain filterChain = new DefaultFilterChain(filters, this.filterChain);
			return filterChain.doFilter(new EmptyInvoker(method), proxy, targetClass, method, args);
		}
	}

	public Class<?> getUserClass(Class<?> clazz) {
		return clazz.getInterfaces()[0];
	}
}
