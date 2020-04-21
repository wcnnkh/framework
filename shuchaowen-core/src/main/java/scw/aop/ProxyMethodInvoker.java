package scw.aop;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

import scw.core.reflect.SerializableMethodHolder;
import scw.lang.NestedExceptionUtils;

/**
 * 一般情况下用来处理静态方法
 * 
 * @author shuchaowen
 *
 */
public abstract class ProxyMethodInvoker extends SerializableMethodHolder implements Invoker, Serializable{
	private static final long serialVersionUID = 1L;
	protected final Collection<Filter> filters;
	protected final FilterChain filterChain;
	protected final Class<?> targetClass;

	public ProxyMethodInvoker(Class<?> targetClass, Method method, Collection<Filter> filters, FilterChain filterChain) {
		super(targetClass, method);
		this.filters = filters;
		this.filterChain = filterChain;
		this.targetClass = targetClass;
	}
	
	protected abstract Object getInstance();

	protected boolean isProxy(Object instance) {
		boolean isProxy = !(Modifier.isPrivate(getMethod().getModifiers())
				|| Modifier.isStatic(getMethod().getModifiers()) || Modifier.isFinal(getMethod().getModifiers())
				|| Modifier.isNative(getMethod().getModifiers()));
		if (isProxy) {
			isProxy = instance != null && ProxyUtils.getProxyAdapter().isProxy(instance.getClass());
		}
		return isProxy;
	}

	public Object invoke(Object... args) throws Throwable {
		Object bean = getInstance();
		if (isProxy(bean)) {
			try {
				return getMethod().invoke(bean, args);
			} catch (Throwable e) {
				throw NestedExceptionUtils.excludeInvalidNestedExcpetion(e);
			}
		}

		FilterChain invoke = new DefaultFilterChain(filters, filterChain);
		return invoke.doFilter(new ReflectInvoker(bean, getMethod()), bean, targetClass, getMethod(), args);
	}

	@Override
	public String toString() {
		return getMethod().toString();
	}

}
