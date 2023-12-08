package io.basc.framework.beans.factory.support;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.beans.factory.BeanProvider;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.Configurable;
import io.basc.framework.beans.factory.config.ConfigurableServiceLoaderFactory;
import io.basc.framework.observe.register.ServiceRegistry;
import io.basc.framework.util.Registration;

public class DefaultServiceLoaderFactory extends DefaultBeanFactory implements ConfigurableServiceLoaderFactory {

	private volatile Map<Class<?>, ServiceRegistry<?>> serviceLoaderMap = new HashMap<>();

	public DefaultServiceLoaderFactory(Scope scope) {
		super(scope);
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
	public <S> ServiceRegistry<S> getServiceLoader(Class<S> serviceClass) {
		ServiceRegistry<S> serviceLoader = (ServiceRegistry<S>) serviceLoaderMap.get(serviceClass);
		if (serviceLoader == null) {
			synchronized (this) {
				serviceLoader = (ServiceRegistry<S>) serviceLoaderMap.get(serviceClass);
				if (serviceLoader == null) {
					serviceLoader = new ServiceRegistry<>();
					postProcessorServiceRegistry(serviceLoader, serviceClass);
					serviceLoaderMap.putIfAbsent(serviceClass, serviceLoader);
				}
			}
		}
		return serviceLoader;
	}

	protected <S> void postProcessorServiceRegistry(ServiceRegistry<S> serviceRegistry, Class<S> serviceClass) {
		BeanProvider<S> beanProvider = getBeanProvider(serviceClass);
		serviceRegistry.registerServiceLoader(beanProvider);
	}
}
