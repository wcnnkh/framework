package io.basc.framework.util.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import io.basc.framework.util.Receipt;
import io.basc.framework.util.ServiceLoader;
import lombok.RequiredArgsConstructor;

public class ConfigurableServices<S> extends Services<S> implements Configurable {
	@RequiredArgsConstructor
	private final class Configuration implements Configured<S>, IncludeWrapper<S, Include<S>> {
		private final Receipt receipt;
		private final ServiceLoaderDiscovery serviceLoaderDiscovery;
		private final Include<S> source;

		@Override
		public boolean cancel() {
			Lock lock = getReadWriteLock().writeLock();
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
		public Include<S> getSource() {
			return source;
		}

		@Override
		public boolean isCancelled() {
			Lock lock = getReadWriteLock().readLock();
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
	}

	private volatile Map<ServiceLoaderDiscovery, Include<S>> discoveryMap;
	private volatile Class<S> serviceClass;

	@Override
	public Receipt doConfigure(ServiceLoaderDiscovery discovery) {
		return doConfigure(discovery, true);
	}

	public Configured<S> doConfigure(ServiceLoaderDiscovery discovery, boolean reloadable) {
		Lock lock = getReadWriteLock().writeLock();
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

		Include<S> include = discoveryMap.get(discovery);
		if (include == null) {
			include = registers(serviceLoader);
			discoveryMap.put(discovery, include);
		} else if (reloadable) {
			include.reload();
		}
		return new Configuration(Receipt.SUCCESS, discovery, include);
	}

	public Class<S> getServiceClass() {
		Lock lock = getReadWriteLock().readLock();
		lock.lock();
		try {
			return serviceClass;
		} finally {
			lock.unlock();
		}
	}

	public void setServiceClass(Class<S> serviceClass) {
		Lock lock = getReadWriteLock().writeLock();
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
