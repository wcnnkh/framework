package io.basc.framework.context.annotation;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.context.support.AcceptClassesLoader;
import io.basc.framework.instance.AbstractServiceLoaderFactory;
import io.basc.framework.instance.ServiceLoader;
import io.basc.framework.instance.support.ServiceLoaders;
import io.basc.framework.util.SmartMap;

public abstract class AbstractProviderServiceLoaderFactory extends AbstractServiceLoaderFactory {
	private SmartMap<Class<?>, ServiceLoader<?>> cacheMap;
	private volatile ClassesLoader providerClassesLoader;

	public AbstractProviderServiceLoaderFactory(boolean cache) {
		if (cache) {
			this.cacheMap = new SmartMap<Class<?>, ServiceLoader<?>>(true);
		}
	}

	protected abstract ClassesLoader getScanClassesLoader();

	public ClassesLoader getProviderClassesLoader() {
		if (providerClassesLoader == null) {
			synchronized (this) {
				if (providerClassesLoader == null) {
					providerClassesLoader = new AcceptClassesLoader(getScanClassesLoader(),
							ProviderClassAccept.INSTANCE, true);
				}
			}
		}
		return providerClassesLoader;
	}

	protected <S> ServiceLoader<S> getInternalServiceLoader(Class<S> serviceClass) {
		ServiceLoader<S> parentServiceLoader = new ProviderServiceLoader<S>(getProviderClassesLoader(), this,
				serviceClass);
		ServiceLoader<S> defaultServiceLoader = super.getServiceLoader(serviceClass);
		ServiceLoader<S> created = new ServiceLoaders<S>(parentServiceLoader, defaultServiceLoader);
		return created;
	}

	@SuppressWarnings("unchecked")
	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		if (cacheMap == null) {
			return getInternalServiceLoader(serviceClass);
		}

		ServiceLoader<?> serviceLoader = cacheMap.get(serviceClass);
		if (serviceLoader == null) {
			if (cacheMap.isConcurrent()) {
				serviceLoader = cacheMap.get(serviceClass);
				if (serviceLoader != null) {
					return (ServiceLoader<S>) serviceLoader;
				}

				ServiceLoader<S> created = getInternalServiceLoader(serviceClass);
				ServiceLoader<?> old = cacheMap.putIfAbsent(serviceClass, created);
				if (old == null) {
					old = created;
				}
				return (ServiceLoader<S>) old;
			} else {
				synchronized (cacheMap) {
					serviceLoader = cacheMap.get(serviceClass);
					if (serviceLoader == null) {
						serviceLoader = getInternalServiceLoader(serviceClass);
						cacheMap.put(serviceClass, serviceLoader);
					}
				}
			}
		}
		return (ServiceLoader<S>) serviceLoader;
	}
}
