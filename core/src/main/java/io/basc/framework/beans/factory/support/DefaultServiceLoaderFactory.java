package io.basc.framework.beans.factory.support;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.beans.factory.BeanProvider;
import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.Configurable;
import io.basc.framework.beans.factory.config.ConfigurableServiceLoaderFactory;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.Registration;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.Services;
import io.basc.framework.util.SpiServiceLoader;

public class DefaultServiceLoaderFactory extends DefaultBeanFactory implements ConfigurableServiceLoaderFactory {

	private volatile Map<Class<?>, Services<?>> serviceLoaderMap = new HashMap<>();

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
	public <S> Services<S> getServiceLoader(Class<S> serviceClass) {
		Services<S> serviceLoader = (Services<S>) serviceLoaderMap.get(serviceClass);
		if (serviceLoader == null) {
			synchronized (this) {
				serviceLoader = (Services<S>) serviceLoaderMap.get(serviceClass);
				if (serviceLoader == null) {
					serviceLoader = new Services<>();
					postProcessorServiceRegistry(serviceLoader, serviceClass);
					serviceLoaderMap.putIfAbsent(serviceClass, serviceLoader);
				}
			}
		}
		return serviceLoader;
	}

	protected <S> void postProcessorServiceRegistry(Services<S> serviceRegistry, Class<S> serviceClass) {
		BeanProvider<S> beanProvider = getBeanProvider(serviceClass);
		serviceRegistry.getServiceLoaders().register(beanProvider);
		ServiceLoader<S> spiServiceLoader = new SpiServiceLoader<>(serviceClass);
		spiServiceLoader = spiServiceLoader
				.convert((elements) -> elements.peek((e) -> getServiceInjectors().inject(e)));
		serviceRegistry.getServiceLoaders().register(spiServiceLoader);
	}

	@Override
	protected void _init() {
		if (getBeanFactoryPostProcessors().isConfigured()) {
			getBeanFactoryPostProcessors().configure(this);
		}

		if (getBeanPostProcessors().isConfigured()) {
			getBeanPostProcessors().configure(this);
		}
		super._init();
	}

	@Override
	public boolean canInstantiated(ResolvableType type) {
		return ReflectionUtils.isInstance(type.getRawClass());
	}

	@Override
	public Object newInstance(ResolvableType type) {
		Object instance = ReflectionUtils.newInstance(type.getRawClass());
		getServiceInjectors().inject(instance);
		return instance;
	}
}
