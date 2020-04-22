package scw.aop;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;

import scw.core.instance.NoArgsInstanceFactory;
import scw.core.reflect.SerializableMethodHolder;
import scw.lang.NestedExceptionUtils;

public abstract class Aop implements ProxyAdapter {
	public abstract Collection<Filter> getFilters();

	protected abstract ProxyAdapter getProxyAdapter();

	public final boolean isSupport(Class<?> clazz) {
		return getProxyAdapter().isSupport(clazz);
	}

	public Proxy proxy(Class<?> clazz, Class<?>[] interfaces, Collection<? extends Filter> filters,
			FilterChain filterChain) {
		return getProxyAdapter().proxy(clazz, interfaces, getFilters(), new DefaultFilterChain(filters, filterChain));
	}

	public final Class<?> getClass(Class<?> clazz, Class<?>[] interfaces) {
		return getProxyAdapter().getClass(clazz, interfaces);
	}

	public final boolean isProxy(Class<?> clazz) {
		return getProxyAdapter().isProxy(clazz);
	}

	public final Class<?> getUserClass(Class<?> clazz) {
		return getProxyAdapter().getUserClass(clazz);
	}

	public final <T> Proxy proxyInstance(Class<? extends T> clazz, T instance, Class<?>[] interfaces,
			Collection<? extends Filter> filters) {
		return proxyInstance(clazz, instance, interfaces, filters, null);
	}

	public final <T> Proxy proxyInstance(Class<? extends T> clazz, T instance, Class<?>[] interfaces,
			Collection<? extends Filter> filters, FilterChain filterChain) {
		return proxy(clazz, interfaces, Arrays.asList(new InstanceFilter(instance)),
				new DefaultFilterChain(filters, filterChain));
	}

	public final Proxy proxy(Class<?> clazz, Class<?>[] interfaces, Collection<? extends Filter> filters) {
		return proxy(clazz, interfaces, filters, null);
	}

	public final <T> MethodInvoker proxyMethod(Class<? extends T> targetClass, T instance, Method method,
			Collection<? extends Filter> filters, FilterChain filterChain) {
		return new DefaultProxyInvoker(targetClass, instance, method, getFilters(),
				new DefaultFilterChain(filters, filterChain));
	}

	public final <T> MethodInvoker proxyMethod(Class<? extends T> targetClass, T instance, Method method,
			Collection<? extends Filter> filters) {
		return proxyMethod(targetClass, instance, method, filters);
	}

	public final MethodInvoker proxyMethod(NoArgsInstanceFactory noArgsInstanceFactory, String instanceName,
			Class<?> targetClass, Method method, Collection<? extends Filter> filters, FilterChain filterChain) {
		return new InstanceFactoryProxyInvoker(noArgsInstanceFactory, instanceName, targetClass, method, filters,
				filterChain);
	}

	public final MethodInvoker proxyMethod(NoArgsInstanceFactory noArgsInstanceFactory, Class<?> targetClass,
			Method method, Collection<? extends Filter> filters, FilterChain filterChain) {
		return proxyMethod(noArgsInstanceFactory, targetClass.getName(), targetClass, method, filters, filterChain);
	}

	public final MethodInvoker proxyMethod(NoArgsInstanceFactory noArgsInstanceFactory, Class<?> targetClass,
			Method method, Collection<? extends Filter> filters) {
		return proxyMethod(noArgsInstanceFactory, targetClass.getName(), targetClass, method, filters, null);
	}

	private static class InstanceFactoryProxyInvoker extends ProxyInvoker {
		private static final long serialVersionUID = 1L;
		private final NoArgsInstanceFactory noArgsInstanceFactory;
		private final String instanceName;

		public InstanceFactoryProxyInvoker(NoArgsInstanceFactory noArgsInstanceFactory, String instanceName,
				Class<?> targetClass, Method method, Collection<? extends Filter> filters, FilterChain filterChain) {
			super(targetClass, method, filters, filterChain);
			this.noArgsInstanceFactory = noArgsInstanceFactory;
			this.instanceName = instanceName;
		}

		public Object getInstance() {
			return noArgsInstanceFactory.getInstance(instanceName);
		}
	}

	private static class DefaultProxyInvoker extends ProxyInvoker {
		private static final long serialVersionUID = 1L;
		private final Object instance;

		public <T> DefaultProxyInvoker(Class<? extends T> targetClass, T instance, Method method,
				Collection<? extends Filter> filters, FilterChain filterChain) {
			super(targetClass, method, filters, filterChain);
			this.instance = instance;
		}

		public Object getInstance() {
			return instance;
		}
	}

	private static abstract class ProxyInvoker extends MethodHolderInvoker {
		private static final long serialVersionUID = 1L;
		protected final Collection<? extends Filter> filters;
		protected final FilterChain filterChain;

		public ProxyInvoker(Class<?> targetClass, Method method, Collection<? extends Filter> filters,
				FilterChain filterChain) {
			super(new SerializableMethodHolder(targetClass, method));
			this.filters = filters;
			this.filterChain = filterChain;
		}

		protected Invoker getInvoker(Object instance) {
			return new ReflectInvoker(instance, getMethod());
		}

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
					return getInvoker(bean).invoke(args);
				} catch (Throwable e) {
					throw NestedExceptionUtils.excludeInvalidNestedExcpetion(e);
				}
			}

			Context context = new Context(bean, getMethodHolder().getBelongClass(), getMethod(), args);
			FilterChain invoke = new DefaultFilterChain(filters, filterChain);
			return invoke.doFilter(getInvoker(bean), context);
		}
	}

	private static class InstanceFilter implements Filter, Serializable {
		private static final long serialVersionUID = 1L;
		private final Object instnace;

		public InstanceFilter(Object instance) {
			this.instnace = instance;
		}

		public Object doFilter(Invoker invoker, Context context, FilterChain filterChain) throws Throwable {
			return filterChain.doFilter(instnace == null ? new EmptyInvoker(context.getMethod())
					: new ReflectInvoker(instnace, context.getMethod()), context);
		}
	}
}
