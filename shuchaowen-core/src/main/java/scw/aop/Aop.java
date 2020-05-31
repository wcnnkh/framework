package scw.aop;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import scw.core.instance.NoArgsInstanceFactory;
import scw.core.reflect.ReflectionUtils;

public abstract class Aop implements ProxyFactory {
	public abstract Collection<Filter> getFilters();

	public abstract ProxyFactory getProxyFactory();

	public final boolean isSupport(Class<?> clazz) {
		return getProxyFactory().isSupport(clazz);
	}

	public Proxy getProxy(Class<?> clazz, Class<?>[] interfaces,
			FilterChain filterChain) {
		return getProxyFactory().getProxy(clazz, interfaces,
				new DefaultFilterChain(getFilters(), filterChain));
	}

	public Proxy getProxy(Class<?> clazz, Class<?>[] interfaces,
			Filter... filters) {
		return getProxy(clazz, interfaces,
				new DefaultFilterChain(Arrays.asList(filters)));
	}

	public final Class<?> getProxyClass(Class<?> clazz, Class<?>[] interfaces) {
		return getProxyFactory().getProxyClass(clazz, interfaces);
	}

	public final boolean isProxy(Class<?> clazz) {
		return getProxyFactory().isProxy(clazz);
	}

	public final Class<?> getUserClass(Class<?> clazz) {
		return getProxyFactory().getUserClass(clazz);
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

	public final <T> Proxy getProxyInstance(Class<? extends T> clazz,
			T instance, Class<?>[] interfaces, FilterChain filterChain) {
		return getProxyFactory()
				.getProxy(
						clazz,
						interfaces,
						new DefaultFilterChain(mergeFilters(new InstanceFilter(
								instance)), filterChain));
	}

	public <T> MethodInvoker getProxyMethod(Class<? extends T> targetClass,
			T instance, Method method, FilterChain filterChain) {
		return new DefaultProxyInvoker(targetClass, instance, method,
				new DefaultFilterChain(getFilters(), filterChain));
	}

	public final MethodInvoker getProxyMethod(
			NoArgsInstanceFactory noArgsInstanceFactory, String instanceName,
			Class<?> targetClass, Method method, FilterChain filterChain) {
		return new InstanceFactoryProxyInvoker(noArgsInstanceFactory,
				instanceName, targetClass, method, new DefaultFilterChain(
						getFilters(), filterChain));
	}

	public final MethodInvoker getProxyMethod(
			NoArgsInstanceFactory noArgsInstanceFactory, Class<?> targetClass,
			Method method, FilterChain filterChain) {
		return getProxyMethod(noArgsInstanceFactory, targetClass.getName(),
				targetClass, method, new DefaultFilterChain(getFilters(),
						filterChain));
	}

	private final class InstanceFactoryProxyInvoker extends MethodProxyInvoker {
		private final NoArgsInstanceFactory instanceFactory;
		private final String instanceName;

		public InstanceFactoryProxyInvoker(
				NoArgsInstanceFactory instanceFactory, String instanceName,
				Class<?> targetClass, Method method, FilterChain filterChain) {
			super(targetClass, method, filterChain);
			this.instanceFactory = instanceFactory;
			this.instanceName = instanceName;
		}

		@Override
		public Object getProxy() {
			return instanceFactory.getInstance(instanceName);
		}
	}

	private final class DefaultProxyInvoker extends MethodProxyInvoker {
		private final Object instance;

		public <T> DefaultProxyInvoker(Class<? extends T> targetClass,
				T instance, Method method, FilterChain filterChain) {
			super(targetClass, method, filterChain);
			this.instance = instance;
		}

		@Override
		public Object getProxy() {
			return instance;
		}
	}

	private static class InstanceProxyInvoker extends ProxyInvoker {
		private Object instance;

		InstanceProxyInvoker(Object instance, Class<?> targetClass,
				Method method) {
			super(targetClass, method);
			this.instance = instance;
		}

		@Override
		public Object getProxy() {
			return instance;
		}

		public Object invoke(Object... args) throws Throwable {
			return ReflectionUtils.invokeMethod(getMethod(), Modifier
					.isStatic(getMethod().getModifiers()) ? null : instance,
					args);
		}
	}

	private abstract class MethodProxyInvoker extends ProxyInvoker {
		protected final FilterChain filterChain;

		public MethodProxyInvoker(Class<?> targetClass, Method method,
				FilterChain filterChain) {
			super(targetClass, method);
			this.filterChain = filterChain;
		}

		protected boolean isProxy(Object instance) {
			boolean isProxy = !(Modifier.isPrivate(getMethod().getModifiers())
					|| Modifier.isStatic(getMethod().getModifiers())
					|| Modifier.isFinal(getMethod().getModifiers()) || Modifier
					.isNative(getMethod().getModifiers()));
			if (isProxy) {
				isProxy = instance != null
						&& getProxyFactory().isProxy(instance.getClass());
			}
			return isProxy;
		}

		public Object invoke(Object... args) throws Throwable {
			Object instance = getProxy();
			InstanceProxyInvoker invoker = new InstanceProxyInvoker(getProxy(),
					getTargetClass(), getMethod());
			if (isProxy(instance)) {
				return invoker.invoke(args);
			}

			return filterChain.doFilter(invoker, args);
		}
	}

	private static final class InstanceFilter implements Filter, Serializable {
		private static final long serialVersionUID = 1L;
		private final Object instance;

		public InstanceFilter(Object instance) {
			this.instance = instance;
		}

		public Object doFilter(ProxyInvoker invoker, Object[] args,
				FilterChain filterChain) throws Throwable {
			return filterChain.doFilter(
					new InstanceProxyInvoker(Modifier.isStatic(invoker
							.getMethod().getModifiers()) ? null : instance,
							invoker.getTargetClass(), invoker.getMethod()),
					args);
		}
	}

	public Class<?> getUserClass(String className, boolean initialize,
			ClassLoader classLoader) throws ClassNotFoundException {
		return getProxyFactory().getUserClass(className, initialize,
				classLoader);
	}

	public boolean isProxy(String className, ClassLoader classLoader) {
		return getProxyFactory().isProxy(className, classLoader);
	}
}
