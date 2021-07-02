package scw.instance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import scw.core.Assert;
import scw.lang.Nullable;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.util.Supplier;

public class ConfigurableServices<T> implements Configurable, Iterable<T> {
	private static Logger LOGGER = LoggerFactory.getLogger(ConfigurableServices.class);
	private final Class<T> serviceClass;
	private volatile ServiceLoaderFactory serviceLoaderFactory;
	private final Consumer<T> consumer;
	private final Supplier<Collection<T>> supplier;
	private Logger logger = LOGGER;

	public ConfigurableServices(Class<T> serviceClass) {
		this(serviceClass, null);
	}

	public ConfigurableServices(Class<T> serviceClass, @Nullable Consumer<T> consumer) {
		this(serviceClass, consumer, () -> new ArrayList<>(8));
	}

	public ConfigurableServices(Class<T> serviceClass, @Nullable Consumer<T> consumer,
			Supplier<Collection<T>> supplier) {
		this.serviceClass = serviceClass;
		this.consumer = consumer;
		this.supplier = supplier;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		Assert.requiredArgument(logger != null, "logger");
		this.logger = logger;
	}

	public Class<T> getServiceClass() {
		return serviceClass;
	}

	protected void aware(T service) {
		if (consumer != null) {
			consumer.accept(service);
		}

		if (serviceLoaderFactory != null) {
			if (service instanceof Configurable) {
				((Configurable) service).configure(serviceLoaderFactory);
			}
		}
	}

	private volatile Collection<T> services;

	public void addService(T service) {
		if (service == null) {
			return;
		}

		aware(service);
		synchronized (this) {
			if (services == null) {
				services = supplier.get();
			}
			boolean success = services.add(service);
			if (success) {
				if (logger.isDebugEnabled()) {
					logger.debug("Add [{}] service {}", serviceClass, service);
				}
			} else {
				logger.warn("Add [{}] service {} fail", serviceClass, service);
			}
		}
	}

	public int size() {
		return services == null ? 0 : services.size();
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public Iterator<T> iterator() {
		if (services == null) {
			return Collections.emptyIterator();
		}
		return services.iterator();
	}

	private volatile List<T> defaultServices;

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		synchronized (this) {
			this.serviceLoaderFactory = serviceLoaderFactory;
			Collection<T> newServices = supplier.get();
			if (this.services != null) {
				newServices.addAll(this.services);
			}
			if (defaultServices != null) {
				newServices.removeAll(defaultServices);
			}

			this.defaultServices = serviceLoaderFactory.getServiceLoader(serviceClass).toList();
			defaultServices.stream().forEach((service) -> aware(service));
			if (logger.isDebugEnabled()) {
				logger.debug("Configure [{}] services {}", serviceClass, defaultServices);
			}
			newServices.addAll(defaultServices);
			this.services = newServices;
		}
	}
}
