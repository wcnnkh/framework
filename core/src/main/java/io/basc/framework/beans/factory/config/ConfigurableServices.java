package io.basc.framework.beans.factory.config;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.core.ParameterizedTypeReference;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.lang.Nullable;
import io.basc.framework.observe.register.ServiceRegistry;
import io.basc.framework.util.Registration;
import io.basc.framework.util.element.ServiceLoader;

public class ConfigurableServices<T> extends ServiceRegistry<T> implements Configurable {
	private Registration configurableRegistration;
	private Class<? extends T> serviceClass;

	@SuppressWarnings("unchecked")
	public ConfigurableServices() {
		try {
			ResolvableType type = ResolvableType.forType(new ParameterizedTypeReference<T>() {
			});
			if (!type.hasGenerics()) {
				this.serviceClass = type.getRawClass() == Object.class ? null : (Class<T>) type.getRawClass();
			}
		} catch (Exception e) {
		}
	}

	public void configure(Class<? extends T> serviceClass, ServiceLoaderFactory serviceLoaderFactory) {
		if (serviceLoaderFactory == null || serviceClass == null) {
			return;
		}

		synchronized (this) {
			ServiceLoader<? extends T> serviceLoader = serviceLoaderFactory.getServiceLoader(serviceClass);
			if (configurableRegistration != null) {
				configurableRegistration.unregister();
			}
			configurableRegistration = getServiceLoaderRegistry().register(serviceLoader);
		}
	}

	public boolean isConfigured() {
		synchronized (this) {
			return configurableRegistration != null;
		}
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		synchronized (this) {
			configure(this.serviceClass, serviceLoaderFactory);
		}
	}

	@Nullable
	public Class<? extends T> getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(Class<? extends T> serviceClass) {
		this.serviceClass = serviceClass;
	}

	@Override
	public String toString() {
		if (this.serviceClass == null) {
			return getServices().toList().toString();
		}
		return "[" + serviceClass.getName() + "] services " + getServices().toList();
	}
}
