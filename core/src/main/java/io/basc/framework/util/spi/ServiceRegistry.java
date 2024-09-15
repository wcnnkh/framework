package io.basc.framework.util.spi;

import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Receipt;
import io.basc.framework.util.Registration;
import io.basc.framework.util.Registrations;
import io.basc.framework.util.Reloadable;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.ServiceLoaderWrapper;
import io.basc.framework.util.event.ChangeEvent;
import io.basc.framework.util.event.ChangeType;
import io.basc.framework.util.register.PayloadRegistration;
import io.basc.framework.util.register.RegistrationException;
import io.basc.framework.util.register.Registry;
import io.basc.framework.util.register.StandardPayloadRegistration;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 服务注册表，一般应用于组件注册
 * 
 * @author shuchaowen
 *
 * @param <S>
 */
public class ServiceRegistry<S> implements Registry<S>, ServiceLoaderWrapper<S, Elements<S>> {
	@RequiredArgsConstructor
	private class InternalServiceLoader implements ServiceLoader<S>, Registration {
		private final Iterable<? extends S> iterable;
		private volatile Registrations<PayloadRegistration<S>> registrations;

		@Override
		public boolean isCancellable() {
			Lock lock = readWriteLock.readLock();
			lock.lock();
			try {
				if (this.registrations == null) {
					return false;
				}

				return this.registrations.isCancellable();
			} finally {
				lock.unlock();
			}
		}

		@Override
		public boolean cancel() {
			Lock lock = readWriteLock.writeLock();
			lock.lock();
			try {
				if (this.registrations == null) {
					return false;
				}

				return this.registrations.cancel();
			} finally {
				lock.unlock();
			}
		}

		@Override
		public boolean isCancelled() {
			Lock lock = readWriteLock.readLock();
			lock.lock();
			try {
				if (this.registrations == null) {
					return true;
				}

				return this.registrations.isCancelled();
			} finally {
				lock.unlock();
			}
		}

		@Override
		public Iterator<S> iterator() {
			Lock lock = readWriteLock.readLock();
			lock.lock();
			try {
				return this.registrations == null ? Collections.emptyIterator()
						: this.registrations.getElements().map((e) -> e.getPayload()).iterator();
			} finally {
				lock.unlock();
			}
		}

		@Override
		public void reload() {
			Lock lock = readWriteLock.writeLock();
			lock.lock();
			try {
				// 不是第一次才reload
				if (registrations != null) {
					if (this.iterable instanceof Reloadable) {
						((Reloadable) this.iterable).reload();
					}
				}

				// 注销所属
				this.cancel();

				// 重新登记
				Elements<PayloadRegistration<S>> elements = Elements.of(this.iterable).map((e) -> {
					Registration registration = ServiceRegistry.this.register(e);
					if (registration.isCancelled()) {
						return PayloadRegistration.cancelled();
					}

					S payload = e;
					return new StandardPayloadRegistration<Registration, S>(registration, payload);
				});

				Elements<PayloadRegistration<S>> list = elements.filter((e) -> !e.isCancelled()).toList();
				this.registrations = () -> list;
			} finally {
				lock.unlock();
			}
		}
	}

	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final ServiceInjectorRegistry<S> serviceInjectorRegistry = new ServiceInjectorRegistry<>(
			this::onServiceInjectorEvents);
	private final ServiceLoaderRegistry<S> serviceLoaderRegistry = new ServiceLoaderRegistry<>();

	@NonNull
	private final Registry<S> registry;

	public ServiceRegistry(@NonNull Registry<S> registry) {
		this.registry = registry;
	}

	public ServiceInjectorRegistry<S> getServiceInjectorRegistry() {
		return serviceInjectorRegistry;
	}

	@Override
	public final Elements<S> getElements() {
		return getSource();
	}

	@Override
	public Elements<S> getSource() {
		Lock lock = readWriteLock.readLock();
		lock.lock();
		try {
			return registry.getElements();
		} finally {
			lock.unlock();
		}
	}

	protected Receipt onServiceInjectorEvents(Elements<ChangeEvent<ServiceInjector<S>>> events) {
		for (ChangeEvent<ServiceInjector<S>> event : events) {
			registry.getElements().forEach((service) -> {
				if (event.getChangeType() != ChangeType.CREATE) {
					return;
				}

				event.getSource().inject(service);
			});
		}
		return Receipt.success();
	}

	@Override
	public Registration register(S element) throws RegistrationException {
		Registration registration = registry.register(element);
		if (registration.isCancelled()) {
			return registration;
		}

		serviceInjectorRegistry.inject(element);
		return registration;
	}

	@Override
	public final Registration registers(Iterable<? extends S> elements) throws RegistrationException {
		InternalServiceLoader serviceLoader = new InternalServiceLoader(elements);
		Registration registration = serviceLoaderRegistry.register(serviceLoader);
		if (registration.isCancelled()) {
			return registration;
		}

		serviceLoader.reload();
		return registration.and(serviceLoader);
	}

	@Override
	public void reload() {
		Lock lock = readWriteLock.writeLock();
		lock.lock();
		try {
			serviceLoaderRegistry.getElements().forEach(ServiceLoader::reload);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public boolean isEmpty() {
		return registry.isEmpty();
	}
}
