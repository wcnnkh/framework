package io.basc.framework.factory.support;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.factory.ConfigurableServiceLoader;
import io.basc.framework.factory.ConfigurableServiceLoaderFactory;
import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.util.Registration;

public class DefaultServiceLoaderFactory extends DefaultInstanceFactory implements ConfigurableServiceLoaderFactory {
	private final Map<Class<?>, ConfigurableServiceLoader<?>> serviceLoaderMap = new HashMap<>();

	protected <S> ServiceLoader<S> getBeforeServiceLoader(Class<S> serviceClass) {
		return getConfigurableServiceLoader(serviceClass);
	}

	protected <S> ServiceLoader<S> getAfterServiceLoader(Class<S> serviceClass) {
		return new SpiServiceLoader<S>(serviceClass, this);
	}

	@Override
	public <S> ServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		return ServiceLoader.concat(getBeforeServiceLoader(serviceClass), getAfterServiceLoader(serviceClass));
	}

	@SuppressWarnings("unchecked")
	public <S> ConfigurableServiceLoader<S> getConfigurableServiceLoader(Class<? extends S> serviceClass) {
		return (ConfigurableServiceLoader<S>) serviceLoaderMap.get(serviceClass);
	}

	@Override
	public <T> Registration registerService(Class<? extends T> type, T service) {
		ConfigurableServiceLoader<T> serviceLoader = getConfigurableServiceLoader(type);
		if (serviceLoader == null) {
			serviceLoader = new ConfigurableServiceLoader<>();
		}
		return serviceLoader.register(service);
	}

}
