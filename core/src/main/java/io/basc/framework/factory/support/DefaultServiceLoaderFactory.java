package io.basc.framework.factory.support;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.factory.DefaultServiceLoader;
import io.basc.framework.factory.ConfigurableServiceLoaderFactory;
import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.util.Registration;

public class DefaultServiceLoaderFactory extends DefaultInstanceFactory implements ConfigurableServiceLoaderFactory {
	private final Map<Class<?>, DefaultServiceLoader<?>> serviceLoaderMap = new HashMap<>();

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
	public <S> DefaultServiceLoader<S> getConfigurableServiceLoader(Class<? extends S> serviceClass) {
		return (DefaultServiceLoader<S>) serviceLoaderMap.get(serviceClass);
	}

	@Override
	public <T> Registration registerService(Class<? extends T> type, T service) {
		DefaultServiceLoader<T> serviceLoader = getConfigurableServiceLoader(type);
		if (serviceLoader == null) {
			serviceLoader = new DefaultServiceLoader<>();
		}
		return serviceLoader.register(service);
	}

}
