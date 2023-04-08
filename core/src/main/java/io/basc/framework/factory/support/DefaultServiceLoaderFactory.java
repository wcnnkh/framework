package io.basc.framework.factory.support;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.factory.ConfigurableServiceLoaderFactory;
import io.basc.framework.util.ConfigurableServiceLoader1;
import io.basc.framework.util.ServiceLoader;

public class DefaultServiceLoaderFactory extends DefaultInstanceFactory implements ConfigurableServiceLoaderFactory {
	private volatile Map<Class<?>, ConfigurableServiceLoader1<?>> serviceLoaderMap = new HashMap<>();

	@SuppressWarnings("unchecked")
	@Override
	public <S> ConfigurableServiceLoader1<S> getServiceLoader(Class<S> serviceClass) {
		ConfigurableServiceLoader1<S> serviceLoader = (ConfigurableServiceLoader1<S>) serviceLoaderMap
				.get(serviceClass);
		if (serviceLoader == null) {
			synchronized (this) {
				serviceLoader = (ConfigurableServiceLoader1<S>) serviceLoaderMap.get(serviceClass);
				if (serviceLoader == null) {
					serviceLoader = new ConfigurableServiceLoader1<>();
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
