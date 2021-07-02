package scw.instance;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import scw.core.Assert;
import scw.core.OrderComparator;
import scw.lang.Nullable;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.util.MultiIterator;

public class ConfigurableServices<T> implements Configurable, Iterable<T> {
	private static Logger LOGGER = LoggerFactory.getLogger(ConfigurableServices.class);
	private volatile List<T> services;
	private volatile List<T> defaultServices;
	private final Class<T> serviceClass;
	private volatile ServiceLoaderFactory serviceLoaderFactory;
	private final Consumer<T> consumer;
	private Logger logger = LOGGER;

	public ConfigurableServices(Class<T> serviceClass) {
		this(serviceClass, null);
	}

	public ConfigurableServices(Class<T> serviceClass, @Nullable Consumer<T> consumer) {
		this.serviceClass = serviceClass;
		this.consumer = consumer;
	}
	
	public void setLogger(Logger logger) {
		Assert.requiredArgument(logger != null, "logger");
		this.logger = logger;
	}

	public int size() {
		int size = 0;
		if (defaultServices != null) {
			size += defaultServices.size();
		}

		if (services != null) {
			size += services.size();
		}
		return size;
	}

	public Class<T> getServiceClass() {
		return serviceClass;
	}

	public void addService(T service) {
		if (service == null) {
			return;
		}

		aware(service);
		synchronized (this) {
			if (services == null) {
				services = new CopyOnWriteArrayList<>();
			}
			services.add(service);
			
			if(logger.isDebugEnabled()) {
				logger.debug("Add [{}] service {}", serviceClass, service);
			}
			
			Collections.sort(services, OrderComparator.INSTANCE);
		}
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

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		synchronized (this) {
			this.serviceLoaderFactory = serviceLoaderFactory;
			this.defaultServices = serviceLoaderFactory.getServiceLoader(serviceClass).toList();
			defaultServices.stream().forEach((service) -> aware(service));
			if(logger.isDebugEnabled()) {
				logger.debug("Configure [{}] services {}", serviceClass, defaultServices);
			}
		}
	}

	@Override
	public Iterator<T> iterator() {
		if (defaultServices == null) {
			if (services == null) {
				return Collections.emptyIterator();
			} else {
				return services.iterator();
			}
		} else {
			if (services == null) {
				return defaultServices.iterator();
			} else {
				return new MultiIterator<>(services.iterator(), defaultServices.iterator());
			}
		}
	}

}
