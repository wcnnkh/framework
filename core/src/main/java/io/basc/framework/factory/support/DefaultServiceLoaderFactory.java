package io.basc.framework.factory.support;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.factory.ConfigurableServiceLoaderFactory;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.ServiceRegistry;

public class DefaultServiceLoaderFactory extends DefaultInstanceFactory implements ConfigurableServiceLoaderFactory {
	private volatile Map<Class<?>, ServiceRegistry<?>> serviceLoaderMap = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public <S> ServiceRegistry<S> getServiceLoader(Class<S> serviceClass) {
		ServiceRegistry<S> serviceLoader = (ServiceRegistry<S>) serviceLoaderMap.get(serviceClass);
		if (serviceLoader == null) {
			synchronized (this) {
				serviceLoader = (ServiceRegistry<S>) serviceLoaderMap.get(serviceClass);
				if (serviceLoader == null) {
					serviceLoader = new ServiceRegistry<>();
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
