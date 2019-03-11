package scw.beans.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;

import scw.beans.proxy.DefaultFilterChain;
import scw.beans.proxy.Filter;
import scw.beans.proxy.FilterChain;
import scw.beans.proxy.Invoker;

public final class JDKProxyUtils {
	private JDKProxyUtils() {
	};

	public static Object newProxyInstance(final Object obj, Class<?> interfaceClass, final Collection<Filter> filters) {
		return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				new InvocationHandler() {

					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						FilterChain filterChain = new DefaultFilterChain(filters);
						return filterChain.doFilter(new JDKInvoker(obj, method, args), proxy, method, args);
					}
				});
	}

	public static Object newProxyInstance(Class<?> interfaceClass, final Collection<Filter> filters,
			final Invoker invoker) {
		return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] { interfaceClass },
				new InvocationHandler() {

					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						FilterChain filterChain = new DefaultFilterChain(filters);
						return filterChain.doFilter(invoker, proxy, method, args);
					}
				});
	}
}
