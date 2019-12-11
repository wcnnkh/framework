package scw.beans;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.aop.ProxyUtils;
import scw.aop.ReflectInvoker;
import scw.core.reflect.ReflectionUtils;
import scw.lang.NestedExceptionUtils;

public final class MethodProxyInvoker implements Invoker {
	private final Method method;
	private final BeanFactory beanFactory;
	private final Class<?> clz;
	private final Object bean;

	private boolean proxy;

	public MethodProxyInvoker(BeanFactory beanFactory, Class<?> clz, Method method) {
		this.beanFactory = beanFactory;
		this.clz = clz;
		this.method = method;
		ReflectionUtils.setAccessibleMethod(method);
		this.bean = beanFactory.isSingleton(clz) ? getBean() : null;
		BeanDefinition beanDefinition = beanFactory.getBeanDefinition(clz.getName());
		proxy = ProxyUtils.isProxy(bean) || (beanDefinition != null && beanDefinition.isProxy());
		if (proxy) {
			if (Modifier.isPrivate(method.getModifiers()) || Modifier.isStatic(method.getModifiers())
					|| Modifier.isFinal(method.getModifiers()) || Modifier.isNative(method.getModifiers())) {
				proxy = false;
			}
		}
	}

	private Object getBean() {
		if (Modifier.isStatic(method.getModifiers())) {
			return null;
		}
		return bean == null ? beanFactory.getInstance(clz) : bean;
	}

	public Object invoke(Object... args) throws Throwable {
		Object bean = getBean();
		if (proxy) {
			try {
				return method.invoke(bean, args);
			} catch (Throwable e) {
				throw NestedExceptionUtils.excludeInvalidNestedExcpetion(e);
			}
		}

		FilterChain filterChain = new BeanFactoryFilterChain(beanFactory, null, clz, method);
		return filterChain.doFilter(new ReflectInvoker(bean, method), bean, clz, method, args);
	}
}
