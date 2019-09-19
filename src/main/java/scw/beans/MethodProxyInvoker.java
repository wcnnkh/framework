package scw.beans;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.core.aop.FilterChain;
import scw.core.aop.Invoker;
import scw.core.aop.ProxyUtils;
import scw.core.aop.ReflectInvoker;

public final class MethodProxyInvoker implements Invoker {
	private final Method method;
	private final BeanFactory beanFactory;
	private boolean proxy;
	private Object bean;
	private final Invoker invoker;
	private Class<?> clz;

	public MethodProxyInvoker(BeanFactory beanFactory, Class<?> clz, Method method) {
		this.bean = Modifier.isStatic(method.getModifiers()) ? null : beanFactory.getInstance(clz);
		proxy = ProxyUtils.isProxy(bean);
		if (proxy) {
			if (Modifier.isPrivate(method.getModifiers()) || Modifier.isStatic(method.getModifiers())
					|| Modifier.isFinal(method.getModifiers()) || Modifier.isNative(method.getModifiers())) {
				proxy = false;
			}
		}

		this.clz = clz;
		invoker = new ReflectInvoker(bean, method);
		this.beanFactory = beanFactory;
		this.method = method;
	}

	public Object invoke(Object... args) throws Throwable {
		if (proxy) {
			return invoker.invoke(args);
		}

		FilterChain filterChain = new BeanFactoryFilterChain(beanFactory, null, clz, method, null);
		return filterChain.doFilter(invoker, bean, method, args);
	}
}
