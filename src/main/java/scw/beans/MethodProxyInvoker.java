package scw.beans;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

import scw.core.aop.FilterChain;
import scw.core.aop.Invoker;
import scw.core.aop.ProxyUtils;
import scw.core.aop.ReflectInvoker;

public final class MethodProxyInvoker implements Invoker {
	private Collection<String> filters;
	private final Method method;
	private final BeanFactory beanFactory;
	private boolean proxy;
	private Object bean;
	private final Invoker invoker;

	public MethodProxyInvoker(BeanFactory beanFactory, Class<?> clz,
			Method method, String[] rootFilters) {
		this.bean = Modifier.isStatic(method.getModifiers()) ? null
				: beanFactory.getInstance(clz);
		proxy = ProxyUtils.isProxy(bean);
		if (proxy) {
			if (Modifier.isPrivate(method.getModifiers())
					|| Modifier.isStatic(method.getModifiers())
					|| Modifier.isFinal(method.getModifiers())
					|| Modifier.isNative(method.getModifiers())) {
				proxy = false;
			}
		}

		invoker = new ReflectInvoker(bean, method);
		this.beanFactory = beanFactory;
		this.method = method;
	}

	public Object invoke(Object... args) throws Throwable {
		if (proxy) {
			return invoker.invoke(args);
		}

		FilterChain filterChain = new BeanFactoryFilterChain(beanFactory,
				filters);
		return filterChain.doFilter(invoker, bean, method, args);
	}
}
