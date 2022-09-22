package io.basc.framework.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import io.basc.framework.core.ParameterizedTypeReference;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Services;

public class ConfigurableServices<T> extends Services<T> implements Configurable, ServiceLoader<T> {
	private static Logger LOGGER = LoggerFactory.getLogger(ConfigurableServices.class);
	private volatile List<? extends T> defaultServices;
	private Logger logger = LOGGER;

	private Class<T> serviceClass;

	private volatile ServiceLoaderFactory serviceLoaderFactory;

	public ConfigurableServices() {
		this(null, null);
	}

	public ConfigurableServices(Class<T> serviceClass) {
		this(serviceClass, null);
	}

	@SuppressWarnings("unchecked")
	public ConfigurableServices(@Nullable Class<T> serviceClass, @Nullable Supplier<Collection<T>> supplier) {
		super(supplier);
		this.serviceClass = serviceClass;
		if (this.serviceClass == null) {
			try {
				ResolvableType type = ResolvableType
						.forType(ParameterizedTypeReference.getParameterizedType(getClass()));
				if (!type.hasGenerics()) {
					this.serviceClass = (Class<T>) type.getRawClass();
				}
			} catch (Exception e) {
			}
		}
	}

	public ConfigurableServices(Supplier<Collection<T>> supplier) {
		this(null, supplier);
	}

	public void configure(Class<? extends T> serviceClass, ServiceLoaderFactory serviceLoaderFactory) {
		if (serviceLoaderFactory == null || serviceClass == null) {
			return;
		}

		synchronized (this) {
			List<T> newServices = new ArrayList<T>();
			newServices.addAll(getTargetServices());
			if (defaultServices != null) {
				newServices.removeAll(defaultServices);
			}
			this.defaultServices = serviceLoaderFactory.getServiceLoader(serviceClass).toList();
			if (logger.isDebugEnabled()) {
				logger.debug("Configure [{}] services {}", serviceClass, defaultServices);
			}

			if (this.defaultServices != null) {
				newServices.addAll(this.defaultServices);
			}

			clear();
			addServices(newServices);
		}
	}

	public boolean isConfigured() {
		synchronized (this) {
			return serviceLoaderFactory != null;
		}
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		synchronized (this) {
			this.serviceLoaderFactory = serviceLoaderFactory;
			configure(this.serviceClass, serviceLoaderFactory);
		}
	}

	public Logger getLogger() {
		return logger;
	}

	@Nullable
	public Class<T> getServiceClass() {
		return serviceClass;
	}

	@Override
	public void reload() {
		if (serviceLoaderFactory != null) {
			configure(serviceLoaderFactory);
		}
	}

	public void setLogger(Logger logger) {
		Assert.requiredArgument(logger != null, "logger");
		this.logger = logger;
	}

	@Override
	public String toString() {
		if (this.serviceClass == null) {
			return super.toString();
		}
		return "[" + serviceClass.getName() + "] services " + super.toString();
	}
}
