package scw.beans;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.aop.ProxyUtils;
import scw.aop.ReflectInvoker;
import scw.core.reflect.ReflectionUtils;
import scw.lang.NestedExceptionUtils;

/**
 * 用来处理静态方法或静态类
 * 
 * @author shuchaowen
 *
 */
public final class AutoProxyMethodInvoker implements Invoker {
	private final Method method;
	private final BeanFactory beanFactory;
	private final Class<?> targetClass;
	private final String beanId;

	public AutoProxyMethodInvoker(BeanFactory beanFactory,
			Class<?> targetClass, Method method) {
		this(beanFactory, targetClass, method, targetClass.getName());
	}

	public AutoProxyMethodInvoker(BeanFactory beanFactory,
			Class<?> targetClass, Method method, String beanId) {
		this.beanFactory = beanFactory;
		this.targetClass = targetClass;
		this.method = method;
		this.beanId = beanId;
		ReflectionUtils.setAccessibleMethod(method);
	}

	public Object invoke(Object... args) throws Throwable {
		Object bean = Modifier.isStatic(method.getModifiers()) ? null
				: beanFactory.getInstance(beanId);
		boolean isProxy = !(Modifier.isPrivate(method.getModifiers())
				|| Modifier.isStatic(method.getModifiers())
				|| Modifier.isFinal(method.getModifiers()) || Modifier
				.isNative(method.getModifiers()));
		if (isProxy) {
			isProxy = bean != null
					&& ProxyUtils.getProxyAdapter().isProxy(bean.getClass());
		}

		if (isProxy) {
			try {
				return method.invoke(bean, args);
			} catch (Throwable e) {
				throw NestedExceptionUtils.excludeInvalidNestedExcpetion(e);
			}
		}

		FilterChain filterChain = new MethodFilterChain(beanFactory,
				targetClass, method, null, null);
		return filterChain.doFilter(new ReflectInvoker(bean, method), bean,
				targetClass, method, args);
	}

	@Override
	public String toString() {
		return method.toString();
	}
}
