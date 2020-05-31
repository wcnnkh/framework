package scw.aop;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;

import scw.aop.ProxyInvoker.AbstractProxyInvoker;
import scw.core.instance.NoArgsInstanceFactory;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.ArrayUtils;

public abstract class Aop implements ProxyFactory {
	public abstract Filter[] getFilters();

	public abstract ProxyFactory getProxyFactory();

	public final boolean isSupport(Class<?> clazz) {
		return getProxyFactory().isSupport(clazz);
	}

	public Proxy getProxy(Class<?> clazz, Class<?>[] interfaces,
			Filter... filters) {
		return getProxyFactory().getProxy(clazz, interfaces,
				mergeFilters(filters));
	}

	public Proxy getProxy(Class<?> clazz, Class<?>[] interfaces,
			Collection<Filter> filters) {
		return getProxy(clazz, interfaces, new MultiFilter(filters));
	}

	public Proxy getProxy(Class<?> clazz, Class<?>[] interfaces,
			NoArgsInstanceFactory instanceFactory,
			Collection<String> filterNames) {
		return getProxy(clazz, interfaces, new MultiFilter(instanceFactory,
				filterNames));
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

	private Filter[] mergeFilters(Filter... filters) {
		if (ArrayUtils.isEmpty(filters)) {
			return getFilters();
		}

		Filter[] globalFilters = getFilters();
		Filter[] filtersToUse = new Filter[globalFilters.length
				+ filters.length];
		System.arraycopy(globalFilters, 0, filtersToUse, 0,
				globalFilters.length);
		System.arraycopy(filters, 0, filtersToUse, globalFilters.length,
				filters.length);
		return filtersToUse;
	}

	public final <T> Proxy getProxyInstance(Class<? extends T> clazz,
			T instance, Class<?>[] interfaces, Filter... filters) {
		return getProxyFactory().getProxy(clazz, interfaces,
				new InstanceFilter(instance, mergeFilters(filters)));
	}

	public final <T> Proxy getProxyInstance(Class<? extends T> clazz,
			T instance, Class<?>[] interfaces, Collection<Filter> filters) {
		return getProxyInstance(clazz, instance, interfaces, new MultiFilter(
				filters));
	}

	public final <T> Proxy getProxyInstance(Class<? extends T> clazz,
			T instance, Class<?>[] interfaces,
			NoArgsInstanceFactory instanceFactory,
			Collection<String> filterNames) {
		return getProxyInstance(clazz, instance, interfaces, new MultiFilter(
				instanceFactory, filterNames));
	}

	public <T> MethodInvoker getProxyMethod(Class<? extends T> targetClass,
			T instance, Method method, Filter... filters) {
		return new DefaultProxyInvoker(targetClass, instance, method,
				mergeFilters(filters));
	}

	public final MethodInvoker getProxyMethod(
			NoArgsInstanceFactory instanceFactory, String instanceName,
			Class<?> targetClass, Method method, Filter... filters) {
		if (Modifier.isStatic(method.getModifiers())) {
			return getProxyMethod(targetClass, null, method, filters);
		}

		if (instanceFactory.isSingleton(instanceName)) {
			return getProxyMethod(targetClass,
					instanceFactory.getInstance(instanceName), method, filters);
		}

		return new InstanceFactoryProxyInvoker(instanceFactory, instanceName,
				targetClass, method, mergeFilters(filters));
	}

	public final MethodInvoker getProxyMethod(
			NoArgsInstanceFactory noArgsInstanceFactory, Class<?> targetClass,
			Method method, Filter... filters) {
		return getProxyMethod(noArgsInstanceFactory, targetClass.getName(),
				targetClass, method, filters);
	}

	private final class InstanceFactoryProxyInvoker extends MethodProxyInvoker {
		private final NoArgsInstanceFactory instanceFactory;
		private final String instanceName;

		public InstanceFactoryProxyInvoker(
				NoArgsInstanceFactory instanceFactory, String instanceName,
				Class<?> targetClass, Method method, Filter[] filters) {
			super(targetClass, method, filters);
			this.instanceFactory = instanceFactory;
			this.instanceName = instanceName;
		}

		public Object getProxy() {
			return instanceFactory.getInstance(instanceName);
		}
	}

	private final class DefaultProxyInvoker extends MethodProxyInvoker {
		private final Object instance;

		public <T> DefaultProxyInvoker(Class<? extends T> targetClass,
				T instance, Method method, Filter[] filters) {
			super(targetClass, method, filters);
			this.instance = instance;
		}

		public Object getProxy() {
			return instance;
		}
	}

	private static class InstanceProxyInvoker extends AbstractProxyInvoker {
		private Object instance;

		InstanceProxyInvoker(Object instance, Class<?> targetClass,
				Method method) {
			super(targetClass, method);
			this.instance = instance;
		}

		public Object getProxy() {
			return instance;
		}

		public Object invoke(Object... args) throws Throwable {
			return ReflectionUtils.invokeMethod(getMethod(), Modifier
					.isStatic(getMethod().getModifiers()) ? null : instance,
					args);
		}
	}

	private abstract class MethodProxyInvoker extends AbstractProxyInvoker {
		protected final Filter[] filters;

		public MethodProxyInvoker(Class<?> targetClass, Method method,
				Filter[] filters) {
			super(targetClass, method);
			this.filters = filters;
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

			return new FilterProxyInvoker(invoker, filters).invoke(args);
		}
	}

	private static final class InstanceFilter implements Filter, Serializable {
		private static final long serialVersionUID = 1L;
		private final Filter[] filters;
		private final Object instance;

		public InstanceFilter(Object instance, Filter[] filters) {
			this.instance = instance;
			this.filters = filters;
		}

		public Object doFilter(ProxyInvoker invoker, Object[] args)
				throws Throwable {
			ProxyInvoker instanceInvoker = new InstanceProxyInvoker(instance,
					invoker.getTargetClass(), invoker.getMethod());
			return new FilterProxyInvoker(instanceInvoker, filters)
					.invoke(args);
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
