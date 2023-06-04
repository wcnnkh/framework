package io.basc.framework.beans.factory.support;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.ConfigurableServiceLoaderFactory;
import io.basc.framework.execution.parameter.ExecutionParametersExtractor;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.ServiceRegistry;
import io.basc.framework.util.SpiServiceLoader;

public class DefaultServiceLoaderFactory extends DefaultBeanFactory implements ConfigurableServiceLoaderFactory {

	public DefaultServiceLoaderFactory(Scope scope, ExecutionParametersExtractor parametersExtractor) {
		super(scope, parametersExtractor);
	}

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
		ServiceLoader<S> listableServiceLoader = new ListableServiceLoader<>(this, serviceClass);
		ServiceLoader<S> spiServiceLoader = new BeanFactoryServiceLoader<S>(serviceClass, this);
		return listableServiceLoader.concat(spiServiceLoader);
	}
}
