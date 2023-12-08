package io.basc.framework.beans.factory.config;

import java.util.Comparator;

import io.basc.framework.beans.factory.ServiceLoaderFactory;
import io.basc.framework.core.OrderComparator;
import io.basc.framework.core.ParameterizedTypeReference;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.lang.Nullable;
import io.basc.framework.observe.register.ServiceRegistry;
import io.basc.framework.util.Registration;
import io.basc.framework.util.element.ServiceLoader;

public class ConfigurableServices<T> extends ServiceRegistry<T> implements Configurable {
	private Registration configurableRegistration;
	private Class<T> serviceClass;

	public ConfigurableServices() {
	}

	public ConfigurableServices(Comparator<? super T> comparator) {
		this(comparator, null);
	}

	public ConfigurableServices(Class<T> serviceClass) {
		this(OrderComparator.INSTANCE, serviceClass);
	}

	@SuppressWarnings("unchecked")
	public ConfigurableServices(Comparator<? super T> comparator, @Nullable Class<T> serviceClass) {
		super(comparator);
		this.serviceClass = serviceClass;
		if (this.serviceClass == null) {
			try {
				ResolvableType type = ResolvableType.forType(new ParameterizedTypeReference<T>() {
				});
				if (!type.hasGenerics()) {
					this.serviceClass = type.getRawClass() == Object.class ? null : (Class<T>) type.getRawClass();
				}
			} catch (Exception e) {
			}
		}
	}

	public void configure(Class<T> serviceClass, ServiceLoaderFactory serviceLoaderFactory) {
		if (serviceLoaderFactory == null || serviceClass == null) {
			return;
		}

		synchronized (this) {
			ServiceLoader<T> serviceLoader = serviceLoaderFactory.getServiceLoader(serviceClass);
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
	public Class<T> getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(Class<T> serviceClass) {
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
