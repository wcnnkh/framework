package io.basc.framework.util.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Stream;

import io.basc.framework.util.collection.ServiceLoader;
import io.basc.framework.util.exchange.Receipt;
import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.spi.Include.IncludeWrapper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class ConfigurableServices<S> extends Services<S> implements Configurable {
	@RequiredArgsConstructor
	private final class Configuration
			implements Configured<ServiceHolder<S>>, IncludeWrapper<ServiceHolder<S>, Include<ServiceHolder<S>>> {
		private final Receipt receipt;
		private final ServiceLoaderDiscovery serviceLoaderDiscovery;
		private final Include<ServiceHolder<S>> source;

		@Override
		public boolean cancel() {
			Lock lock = getContainer().writeLock();
			lock.lock();
			try {
				if (discoveryMap == null) {
					return false;
				}

				if (discoveryMap.remove(serviceLoaderDiscovery) != null) {
					return IncludeWrapper.super.cancel();
				}
				return false;
			} finally {
				lock.unlock();
			}
		}

		@Override
		public Throwable cause() {
			return receipt.cause();
		}

		@Override
		public Include<ServiceHolder<S>> getSource() {
			return source;
		}

		@Override
		public boolean isCancelled() {
			Lock lock = getContainer().readLock();
			lock.lock();
			try {
				return discoveryMap == null ? false : discoveryMap.containsKey(serviceLoaderDiscovery);
			} finally {
				lock.unlock();
			}
		}

		@Override
		public boolean isDone() {
			return receipt.isDone();
		}

		@Override
		public boolean isSuccess() {
			return receipt.isSuccess();
		}

		@Override
		public Configured<ServiceHolder<S>> and(Registration registration) {
			return Configured.super.and(registration);
		}

		@Override
		public <U> Configured<U> convert(
				@NonNull Function<? super Stream<ServiceHolder<S>>, ? extends Stream<U>> converter) {
			return Configured.super.convert(converter);
		}
	}

	private volatile Map<ServiceLoaderDiscovery, Include<ServiceHolder<S>>> discoveryMap;
	private volatile Class<S> serviceClass;

	@Override
	public Receipt doConfigure(ServiceLoaderDiscovery discovery) {
		return doConfigure(discovery, true);
	}

	public Configured<ServiceHolder<S>> doConfigure(ServiceLoaderDiscovery discovery, boolean reloadable) {
		Lock lock = getContainer().writeLock();
		lock.lock();
		try {
			if (serviceClass == null) {
				return Configured.failure();
			}

			ServiceLoader<S> serviceLoader = discovery.getServiceLoader(serviceClass);
			if (serviceLoader == null) {
				return Configured.failure();
			}

			return doConfigure(discovery, serviceLoader, reloadable);
		} finally {
			lock.unlock();
		}
	}

	private Configuration doConfigure(ServiceLoaderDiscovery discovery, ServiceLoader<S> serviceLoader,
			boolean reloadable) {
		if (discoveryMap == null) {
			discoveryMap = new HashMap<>(2, 1);
		}

		Include<ServiceHolder<S>> include = discoveryMap.get(discovery);
		if (include == null) {
			include = registers(serviceLoader);
			discoveryMap.put(discovery, include);
		} else if (reloadable) {
			include.reload();
		}
		return new Configuration(Receipt.SUCCESS, discovery, include);
	}

	public Class<S> getServiceClass() {
		Lock lock = getContainer().readLock();
		lock.lock();
		try {
			return serviceClass;
		} finally {
			lock.unlock();
		}
	}

	public void setServiceClass(Class<S> serviceClass) {
		Lock lock = getContainer().writeLock();
		lock.lock();
		try {
			if (this.serviceClass == serviceClass) {
				return;
			}

			this.serviceClass = serviceClass;
			// 如果已经初始化了需要reload
		} finally {
			lock.unlock();
		}
	}
}
