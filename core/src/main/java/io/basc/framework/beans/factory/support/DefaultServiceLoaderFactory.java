package io.basc.framework.beans.factory.support;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.ConfigurableServiceLoaderFactory;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.ServiceRegistry;

public class DefaultServiceLoaderFactory extends DefaultBeanFactory implements ConfigurableServiceLoaderFactory {

	private volatile Map<Class<?>, ServiceRegistry<?>> serviceLoaderMap = new HashMap<>();

	public DefaultServiceLoaderFactory(Scope scope) {
		super(scope);
	}

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
		ServiceLoader<S> listableServiceLoader = new ListableServiceLoader<>(this, serviceClass);
		ServiceLoader<S> spiServiceLoader = new BeanFactorySpiServiceLoader<S>(serviceClass, this);
		return listableServiceLoader.concat(spiServiceLoader);
	}
}
