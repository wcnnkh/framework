package io.basc.framework.factory.support;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.factory.ConfigurableServiceLoaderFactory;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.Services;

public class DefaultServiceLoaderFactory extends DefaultInstanceFactory implements ConfigurableServiceLoaderFactory {
	private volatile Map<Class<?>, Services<?>> serviceLoaderMap = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public <S> Services<S> getServiceLoader(Class<S> serviceClass) {
		Services<S> serviceLoader = (Services<S>) serviceLoaderMap.get(serviceClass);
		if (serviceLoader == null) {
			synchronized (this) {
				serviceLoader = (Services<S>) serviceLoaderMap.get(serviceClass);
				if (serviceLoader == null) {
					serviceLoader = new Services<>();
					serviceLoaderMap.putIfAbsent(serviceClass, serviceLoader);
				}
				serviceLoader.getServiceLoaders().register(getServiceLoaderInternal(serviceClass));
			}
		}
		return serviceLoader;
	}

	protected <S> ServiceLoader<S> getServiceLoaderInternal(Class<S> serviceClass) {
		return new SpiServiceLoader<S>(serviceClass, this);
	}
}
