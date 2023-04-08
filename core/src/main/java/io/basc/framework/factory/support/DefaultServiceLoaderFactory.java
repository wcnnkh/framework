package io.basc.framework.factory.support;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.factory.ConfigurableServiceLoaderFactory;
import io.basc.framework.util.CacheServiceLoader;
import io.basc.framework.util.ServiceLoader;

public class DefaultServiceLoaderFactory extends DefaultInstanceFactory implements ConfigurableServiceLoaderFactory {
	private volatile Map<Class<?>, CacheServiceLoader<?>> serviceLoaderMap = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public <S> CacheServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		CacheServiceLoader<S> serviceLoader = (CacheServiceLoader<S>) serviceLoaderMap
				.get(serviceClass);
		if (serviceLoader == null) {
			synchronized (this) {
				serviceLoader = (CacheServiceLoader<S>) serviceLoaderMap.get(serviceClass);
				if (serviceLoader == null) {
					serviceLoader = new CacheServiceLoader<>();
					serviceLoaderMap.putIfAbsent(serviceClass, serviceLoader);
				}
				serviceLoader.register(getServiceLoaderInternal(serviceClass));
			}
		}
		return serviceLoader;
	}

	protected <S> ServiceLoader<S> getServiceLoaderInternal(Class<S> serviceClass) {
		return new SpiServiceLoader<S>(serviceClass, this);
	}
}
