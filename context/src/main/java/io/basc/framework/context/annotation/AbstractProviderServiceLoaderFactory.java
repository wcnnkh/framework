package io.basc.framework.context.annotation;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.context.support.AcceptClassesLoader;
import io.basc.framework.factory.AbstractServiceLoaderFactory;
import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.factory.support.ServiceLoaders;
import io.basc.framework.util.ConcurrentReferenceHashMap;

public abstract class AbstractProviderServiceLoaderFactory extends AbstractServiceLoaderFactory {
	private ConcurrentReferenceHashMap<Class<?>, ServiceLoader<?>> cacheMap;
	private volatile ClassesLoader providerClassesLoader;

	public AbstractProviderServiceLoaderFactory(boolean cache) {
		if (cache) {
			this.cacheMap = new ConcurrentReferenceHashMap<Class<?>, ServiceLoader<?>>(64);
		}
	}

	protected abstract ClassesLoader getScanClassesLoader();

	public ClassesLoader getProviderClassesLoader() {
		if (providerClassesLoader == null) {
			synchronized (this) {
				if (providerClassesLoader == null) {
					providerClassesLoader = new AcceptClassesLoader(getScanClassesLoader(),
							ProviderClassAccept.INSTANCE);
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
			serviceLoader = cacheMap.get(serviceClass);
			if (serviceLoader != null) {
				return (ServiceLoader<S>) serviceLoader;
			}

			ServiceLoader<S> created = getInternalServiceLoader(serviceClass);
			ServiceLoader<?> old = cacheMap.putIfAbsent(serviceClass, created);
			if (old == null) {
				old = created;
			} else {
				// 出现新的时清理缓存
				cacheMap.purgeUnreferencedEntries();
			}
			return (ServiceLoader<S>) old;
		}
		return (ServiceLoader<S>) serviceLoader;
	}
}
