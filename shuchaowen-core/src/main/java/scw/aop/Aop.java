package scw.aop;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import scw.core.instance.NoArgsInstanceFactory;
import scw.core.reflect.SerializableMethodHolder;
import scw.lang.NestedExceptionUtils;

public abstract class Aop implements ProxyAdapter {
	public abstract Collection<Filter> getFilters();

	protected abstract ProxyAdapter getProxyAdapter();

	public final boolean isSupport(Class<?> clazz) {
		return getProxyAdapter().isSupport(clazz);
	}

	public Proxy proxy(Class<?> clazz, Class<?>[] interfaces,
			FilterChain filterChain) {
		return getProxyAdapter().proxy(clazz, interfaces,
				new DefaultFilterChain(getFilters(), filterChain));
	}
	
	public Proxy proxy(Class<?> clazz, Class<?>[] interfaces, Filter ...filters){
		return proxy(clazz, interfaces, new DefaultFilterChain(Arrays.asList(filters)));
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

	protected final LinkedList<Filter> mergeFilters(Filter... filters) {
		LinkedList<Filter> filterList = new LinkedList<Filter>();
		if (filters != null) {
			for (Filter filter : filters) {
				filterList.add(filter);
			}
		}

		Collection<Filter> roots = getFilters();
		if (roots != null) {
			filterList.addAll(roots);
		}
		return filterList;
	}

	public final <T> Proxy proxyInstance(Class<? extends T> clazz, T instance,
			Class<?>[] interfaces, FilterChain filterChain) {
		return getProxyAdapter().proxy(clazz, interfaces, new DefaultFilterChain(mergeFilters(new InstanceFilter(instance)), filterChain));
	}

	public <T> MethodInvoker proxyMethod(Class<? extends T> targetClass,
			T instance, Method method, FilterChain filterChain) {
		return new DefaultProxyInvoker(targetClass, instance, method,
				new DefaultFilterChain(getFilters(), filterChain));
	}

	public final MethodInvoker proxyMethod(
			NoArgsInstanceFactory noArgsInstanceFactory, String instanceName,
			Class<?> targetClass, Method method, FilterChain filterChain) {
		return new InstanceFactoryProxyInvoker(noArgsInstanceFactory,
				instanceName, targetClass, method, new DefaultFilterChain(getFilters(), filterChain));
	}

	public final MethodInvoker proxyMethod(
			NoArgsInstanceFactory noArgsInstanceFactory, Class<?> targetClass,
			Method method, FilterChain filterChain) {
		return proxyMethod(noArgsInstanceFactory, targetClass.getName(),
				targetClass, method, new DefaultFilterChain(getFilters(), filterChain));
	}

	private static class InstanceFactoryProxyInvoker extends ProxyInvoker {
		private static final long serialVersionUID = 1L;
		private final NoArgsInstanceFactory noArgsInstanceFactory;
		private final String instanceName;

		public InstanceFactoryProxyInvoker(
				NoArgsInstanceFactory noArgsInstanceFactory,
				String instanceName, Class<?> targetClass, Method method,
				FilterChain filterChain) {
			super(targetClass, method, filterChain);
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

		public <T> DefaultProxyInvoker(Class<? extends T> targetClass,
				T instance, Method method, FilterChain filterChain) {
			super(targetClass, method, filterChain);
			this.instance = instance;
		}

		public Object getInstance() {
			return instance;
		}
	}

	private static abstract class ProxyInvoker extends MethodHolderInvoker {
		private static final long serialVersionUID = 1L;
		protected final FilterChain filterChain;

		public ProxyInvoker(Class<?> targetClass, Method method,
				FilterChain filterChain) {
			super(new SerializableMethodHolder(targetClass, method));
			this.filterChain = filterChain;
		}

		protected Invoker getInvoker(Object instance) {
			return new ReflectInvoker(instance, getMethod());
		}

		protected boolean isProxy(Object instance) {
			boolean isProxy = !(Modifier.isPrivate(getMethod().getModifiers())
					|| Modifier.isStatic(getMethod().getModifiers())
					|| Modifier.isFinal(getMethod().getModifiers()) || Modifier
					.isNative(getMethod().getModifiers()));
			if (isProxy) {
				isProxy = instance != null
						&& ProxyUtils.getProxyAdapter().isProxy(
								instance.getClass());
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

			Context context = new Context(bean, getMethodHolder()
					.getBelongClass(), getMethod(), args);
			return filterChain.doFilter(getInvoker(bean), context);
		}
	}

	private static final class InstanceFilter implements Filter, Serializable {
		private static final long serialVersionUID = 1L;
		private final Object instance;

		public InstanceFilter(Object instance) {
			this.instance = instance;
		}

		public Object doFilter(Invoker invoker, Context context,
				FilterChain filterChain) throws Throwable {
			Invoker nextInvoker;
			if (Modifier.isStatic(context.getMethod().getModifiers())) {
				nextInvoker = new ReflectInvoker(instance, context.getMethod());
			} else {
				nextInvoker = instance == null ? invoker : new ReflectInvoker(
						instance, context.getMethod());
			}

			return filterChain.doFilter(nextInvoker, context);
		}
	}
}
