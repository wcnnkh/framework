package run.soeasy.framework.util.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.util.collection.Provider;
import run.soeasy.framework.util.exchange.Receipt;
import run.soeasy.framework.util.exchange.Registration;
import run.soeasy.framework.util.spi.Include.IncludeWrapper;

public class ConfigurableServices<S> extends Services<S> implements Configurable {
	@RequiredArgsConstructor
	private final class Configuration
			implements Configured<ServiceHolder<S>>, IncludeWrapper<ServiceHolder<S>, Include<ServiceHolder<S>>> {
		private final Receipt receipt;
		private final ProviderFactory serviceLoaderDiscovery;
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
		public <U> Configured<U> convert(boolean resize,
				@NonNull Function<? super Stream<ServiceHolder<S>>, ? extends Stream<U>> converter) {
			return Configured.super.convert(resize, converter);
		}
	}

	private volatile Map<ProviderFactory, Include<ServiceHolder<S>>> discoveryMap;
	private volatile Class<S> serviceClass;

	@Override
	public Receipt configure(ProviderFactory discovery) {
		return configure(discovery, true);
	}

	public Configured<ServiceHolder<S>> configure(ProviderFactory discovery, boolean reloadable) {
		Lock lock = getContainer().writeLock();
		lock.lock();
		try {
			if (serviceClass == null) {
				return Configured.failure();
			}

			Provider<S> serviceLoader = discovery.getProvider(serviceClass);
			if (serviceLoader == null) {
				return Configured.failure();
			}

			return configure(discovery, serviceLoader, reloadable);
		} finally {
			lock.unlock();
		}
	}

	private Configuration configure(ProviderFactory discovery, Provider<S> serviceLoader, boolean reloadable) {
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
