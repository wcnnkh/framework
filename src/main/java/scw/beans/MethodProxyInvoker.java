package scw.beans;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

import scw.core.aop.FilterChain;
import scw.core.aop.Invoker;
import scw.core.aop.ReflectInvoker;
import scw.core.utils.ClassUtils;

public final class MethodProxyInvoker implements Invoker {
	private Collection<String> filters;
	private final Class<?> clz;
	private final Method method;
	private final BeanFactory beanFactory;
	private boolean proxy;

	public MethodProxyInvoker(BeanFactory beanFactory, Class<?> clz, Method method, String[] rootFilters) {
		this.proxy = beanFactory.isProxy(ClassUtils.getUserClass(clz).getName());
		if (proxy) {
			if (Modifier.isPrivate(method.getModifiers()) || Modifier.isStatic(method.getModifiers())
					|| Modifier.isFinal(method.getModifiers()) || Modifier.isNative(method.getModifiers())) {
				proxy = false;
			}
		}
		this.beanFactory = beanFactory;
		this.method = method;
		this.clz = clz;
		if (!proxy) {
			this.filters = BeanUtils.getBeanFilterNameList(clz, method, rootFilters);
		}
		
	}

	public Object invoke(Object... args) throws Throwable {
		if (proxy) {
			return BeanUtils.getInvoker(beanFactory, clz, method).invoke(args);
		}

		Object bean = Modifier.isStatic(method.getModifiers()) ? null : beanFactory.getInstance(clz);
		Invoker invoker = new ReflectInvoker(bean, method);
		FilterChain filterChain = new BeanFactoryFilterChain(beanFactory, filters);
		return filterChain.doFilter(invoker, bean, method, args);
	}
}
