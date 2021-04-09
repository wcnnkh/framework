package scw.context.support;

import scw.context.ClassesLoader;
import scw.context.ProviderLoaderFactory;
import scw.context.annotation.ProviderClassAccept;
import scw.context.annotation.ProviderServiceLoader;
import scw.context.locks.LockMethodInterceptor;
import scw.context.transaction.TransactionMethodInterceptor;
import scw.env.Environment;
import scw.instance.NoArgsInstanceFactory;
import scw.instance.ServiceLoader;
import scw.instance.ServiceLoaderFactory;
import scw.instance.support.DefaultServiceLoaderFactory;
import scw.instance.support.ServiceLoaders;
import scw.util.SmartMap;

public class DefaultProviderLoaderFactory extends DefaultProviderClassesLoaderFactory implements ProviderLoaderFactory {
	private final SmartMap<Class<?>, ServiceLoader<?>> cacheMap;
	private final NoArgsInstanceFactory instanceFactory;
	protected volatile ClassesLoader<?> serviceClassesLoader;
	private final ServiceLoaderFactory serviceLoaderFactory;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DefaultProviderLoaderFactory(boolean concurrent, boolean cache, Environment environment,
			NoArgsInstanceFactory instanceFactory) {
		super(cache, instanceFactory);
		this.cacheMap = new SmartMap<Class<?>, ServiceLoader<?>>(concurrent);
		this.serviceLoaderFactory = new DefaultServiceLoaderFactory(instanceFactory, environment);
		this.instanceFactory = instanceFactory;

		// 添加默认的类
		getContextClassesLoader().add((Class) TransactionMethodInterceptor.class);
		getContextClassesLoader().add((Class) LockMethodInterceptor.class);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ClassesLoader<?> getServiceClassesLoader() {
		if (serviceClassesLoader == null) {
			synchronized (this) {
				if (serviceClassesLoader == null) {
					serviceClassesLoader = new AcceptClassesLoader(getContextClassesLoader(),
							ProviderClassAccept.INSTANCE, true);
				}
			}
		}
		return serviceClassesLoader;
	}

	@SuppressWarnings("unchecked")
	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		ServiceLoader<?> serviceLoader = cacheMap.get(serviceClass);
		if (serviceLoader == null) {
			if (cacheMap.isConcurrent()) {
				serviceLoader = cacheMap.get(serviceClass);
				if (serviceLoader != null) {
					return (ServiceLoader<S>) serviceLoader;
				}

				ServiceLoader<S> parentServiceLoader = new ProviderServiceLoader<S>(getServiceClassesLoader(),
						instanceFactory, serviceClass);
				ServiceLoader<S> defaultServiceLoader = serviceLoaderFactory.getServiceLoader(serviceClass);
				ServiceLoader<S> created = new ServiceLoaders<S>(parentServiceLoader, defaultServiceLoader);
				ServiceLoader<?> old = cacheMap.putIfAbsent(serviceClass, created);
				if (old == null) {
					old = created;
				}
				return (ServiceLoader<S>) old;
			} else {
				synchronized (cacheMap) {
					serviceLoader = cacheMap.get(serviceClass);
					if (serviceLoader == null) {
						ServiceLoader<S> parentServiceLoader = new ProviderServiceLoader<S>(getServiceClassesLoader(),
								instanceFactory, serviceClass);
						ServiceLoader<S> defaultServiceLoader = serviceLoaderFactory.getServiceLoader(serviceClass);
						serviceLoader = new ServiceLoaders<S>(parentServiceLoader, defaultServiceLoader);
						cacheMap.put(serviceClass, serviceLoader);
					}
				}
			}

		}
		return (ServiceLoader<S>) serviceLoader;
	}
}
