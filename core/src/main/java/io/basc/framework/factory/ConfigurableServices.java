package io.basc.framework.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.MultiIterator;
import io.basc.framework.util.Supplier;

public class ConfigurableServices<T> implements Configurable, Iterable<T> {
	private static Logger LOGGER = LoggerFactory
			.getLogger(ConfigurableServices.class);
	private final Class<T> serviceClass;
	private volatile ServiceLoaderFactory serviceLoaderFactory;
	private final Consumer<T> consumer;
	private final Supplier<Collection<T>> supplier;
	private Logger logger = LOGGER;
	private T beforeService;
	private T afterService;

	public ConfigurableServices(Class<T> serviceClass) {
		this(serviceClass, null);
	}

	public ConfigurableServices(Class<T> serviceClass,
			@Nullable Consumer<T> consumer) {
		this(serviceClass, consumer, () -> new ArrayList<>(8));
	}

	public ConfigurableServices(Class<T> serviceClass,
			@Nullable Consumer<T> consumer, Supplier<Collection<T>> supplier) {
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

	public T getBeforeService() {
		return beforeService;
	}

	/**
	 * 设置前置服务
	 * @param beforeService
	 */
	public void setBeforeService(T beforeService) {
		this.beforeService = beforeService;
	}

	public T getAfterService() {
		return afterService;
	}

	/**
	 * 设置后置服务
	 * @param afterService
	 */
	public void setAfterService(T afterService) {
		this.afterService = afterService;
	}

	public Class<T> getServiceClass() {
		return serviceClass;
	}
	
	protected void aware(T service) {
		if (consumer != null) {
			consumer.accept(service);
		}
	}

	protected void aware(T service, boolean reaware) {
		aware(service);
		
		if(!reaware) {
			if (serviceLoaderFactory != null) {
				if (service instanceof Configurable) {
					((Configurable) service).configure(serviceLoaderFactory);
				}
			}
		}
	}

	private volatile Collection<T> services;
	
	public void aware() {
		synchronized (this) {
			if(services == null) {
				return ;
			}
			
			for(T service : services) {
				aware(service, true);
			}
		}
	}

	public boolean addService(T service) {
		if (service == null) {
			return false;
		}

		aware(service, false);
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
			return success;
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
		T before = getBeforeService();
		T after = getAfterService();
		return new MultiIterator<T>(
				before == null ? Collections.emptyIterator() : Arrays.asList(
						before).iterator(),
				services == null ? Collections.emptyIterator() : services
						.iterator(),
				after == null ? Collections.emptyIterator() : Arrays.asList(
						after).iterator());
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

			this.defaultServices = serviceLoaderFactory.getServiceLoader(
					serviceClass).toList();
			defaultServices.stream().forEach((service) -> aware(service, false));
			if (logger.isDebugEnabled()) {
				logger.debug("Configure [{}] services {}", serviceClass,
						defaultServices);
			}
			newServices.addAll(defaultServices);
			this.services = newServices;
		}
	}

	@Override
	public String toString() {
		return "[" + serviceClass.getName() + "] services " + services == null ? "[]"
				: services.toString();
	}
}
