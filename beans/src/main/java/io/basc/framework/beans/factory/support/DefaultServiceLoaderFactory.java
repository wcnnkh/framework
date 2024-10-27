package io.basc.framework.beans.factory.support;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.beans.factory.BeanProvider;
import io.basc.framework.beans.factory.config.Configurable;
import io.basc.framework.beans.factory.config.ConfigurableServiceLoaderFactory;
import io.basc.framework.observe.service.ObservableServiceLoader;
import io.basc.framework.util.register.Registration;

public class DefaultServiceLoaderFactory extends DefaultBeanFactory implements ConfigurableServiceLoaderFactory {

	private volatile Map<Class<?>, ObservableServiceLoader<?>> serviceLoaderMap = new HashMap<>();

	public DefaultServiceLoaderFactory() {
		getServiceInjectors().register((bean) -> {
			if (bean instanceof Configurable) {
				Configurable configurable = (Configurable) bean;
				if (!configurable.isConfigured()) {
					configurable.configure(this);
				}
			}
			return Registration.EMPTY;
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S> ObservableServiceLoader<S> getServiceLoader(Class<S> serviceClass) {
		ObservableServiceLoader<S> serviceLoader = (ObservableServiceLoader<S>) serviceLoaderMap.get(serviceClass);
		if (serviceLoader == null) {
			synchronized (this) {
				serviceLoader = (ObservableServiceLoader<S>) serviceLoaderMap.get(serviceClass);
				if (serviceLoader == null) {
					serviceLoader = new ObservableServiceLoader<>();
					postProcessorServiceRegistry(serviceLoader, serviceClass);
					serviceLoaderMap.putIfAbsent(serviceClass, serviceLoader);
				}
			}
		}
		return serviceLoader;
	}

	protected <S> void postProcessorServiceRegistry(ObservableServiceLoader<S> serviceRegistry, Class<S> serviceClass) {
		BeanProvider<S> beanProvider = getBeanProvider(serviceClass);
		serviceRegistry.registerServiceLoader(beanProvider);
	}
}
