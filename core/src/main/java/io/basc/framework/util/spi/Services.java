package io.basc.framework.util.spi;

import java.util.concurrent.locks.Lock;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Lifecycle;
import io.basc.framework.util.Listener;
import io.basc.framework.util.Receipt;
import io.basc.framework.util.Registration;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.actor.ChangeType;
import io.basc.framework.util.register.container.AtomicElementRegistration;
import lombok.RequiredArgsConstructor;

public class Services<S> extends ServiceContainer<S> implements ServiceInjector<S> {
	private final ServiceInjectors<S> injectors = new ServiceInjectors<>();

	public Services() {
		injectors.setPublisher(this::onServiceInjectorEvents);
	}

	protected Receipt onServiceInjectorEvents(Elements<ChangeEvent<ServiceInjector<S>>> events) {
		for (ChangeEvent<ServiceInjector<S>> event : events) {
			forEach((service) -> {
				if (event.getChangeType() != ChangeType.CREATE) {
					return;
				}

				event.getSource().inject(service);
			});
		}
		return Receipt.SUCCESS;
	}

	public ServiceInjectors<S> getInjectors() {
		return injectors;
	}

	@RequiredArgsConstructor
	private class InjectListener implements Listener<Lifecycle> {
		private final S service;
		private volatile Registration registration;

		@Override
		public void accept(Lifecycle source) {
			Lock lock = getReadWriteLock().writeLock();
			lock.lock();
			try {
				if (source.isRunning()) {
					if (registration != null) {
						registration.cancel();
					}
					registration = inject(service);
				} else {
					if (registration != null) {
						registration.cancel();
						registration = null;
					}
				}
			} finally {
				lock.unlock();
			}
		}
	}

	@Override
	protected AtomicElementRegistration<S> newElementRegistration(S element) {
		AtomicElementRegistration<S> registration = super.newElementRegistration(element);
		registration.registerListener(new InjectListener(element));
		return registration;
	}

	@Override
	public Registration inject(S service) {
		return injectors.inject(service);
	}
}
