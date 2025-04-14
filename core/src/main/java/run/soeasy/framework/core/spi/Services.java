package run.soeasy.framework.core.spi;

import java.util.Comparator;
import java.util.concurrent.locks.Lock;

import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.Lifecycle;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.Provider.ReloadableElementsWrapper;
import run.soeasy.framework.core.comparator.OrderComparator;
import run.soeasy.framework.core.exchange.Listener;
import run.soeasy.framework.core.exchange.Publisher;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.Receipts;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.container.AtomicElementRegistration;
import run.soeasy.framework.core.exchange.container.Container;
import run.soeasy.framework.core.exchange.container.PayloadRegistration;
import run.soeasy.framework.core.exchange.container.RegistrationException;
import run.soeasy.framework.core.exchange.event.ChangeEvent;
import run.soeasy.framework.core.exchange.event.ChangeType;

public class Services<S>
		implements Container<S, PayloadRegistration<S>>, ReloadableElementsWrapper<S, Elements<S>>, ServiceInjector<S> {
	@RequiredArgsConstructor
	private class InjectListener implements Listener<Lifecycle> {
		private final ServiceHolder<S> holder;
		private volatile Registration registration;

		@Override
		public void accept(Lifecycle source) {
			Lock lock = container.writeLock();
			lock.lock();
			try {
				if (source.isRunning()) {
					if (registration != null) {
						registration.cancel();
					}
					registration = inject(holder.getSource());
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

	private final ServiceContainer<ServiceHolder<S>> container;
	private int defaultOrder = 0;
	private volatile S first;
	private final ServiceInjectors<S> injectors = new ServiceInjectors<>();
	private volatile S last;

	private volatile Publisher<? super Elements<ChangeEvent<S>>> publisher = Publisher.empty();

	public Services() {
		container = new ServiceContainer<ServiceHolder<S>>() {
			@Override
			protected AtomicElementRegistration<ServiceHolder<S>> newElementRegistration(ServiceHolder<S> element) {
				AtomicElementRegistration<ServiceHolder<S>> registration = super.newElementRegistration(element);
				registration.registerListener(new InjectListener(element));
				return registration;
			}
		};
		setComparator(OrderComparator.INSTANCE);
		container.setPublisher((events) -> publisher.publish(events.map((e) -> e.convert((s) -> s.getSource()))));
		injectors.setPublisher(this::onServiceInjectorEvents);
	}

	@Override
	public Receipt deregisters(Elements<? extends S> elements) {
		Elements<Receipt> receipts = container.getElements()
				.filter((e) -> elements.contains(e.getPayload().getSource()))
				.map((e) -> e.cancel() ? Receipt.SUCCESS : Receipt.FAILURE).toList();
		return Receipts.of(receipts);
	}

	public int getDefaultOrder() {
		return defaultOrder;
	}

	@Override
	public Elements<PayloadRegistration<S>> getElements() {
		return container.getElements().map((e) -> e.map((h) -> h.getSource()));
	}

	public S getFirst() {
		return first;
	}

	public ServiceInjectors<S> getInjectors() {
		return injectors;
	}

	public S getLast() {
		return last;
	}

	public Publisher<? super Elements<ChangeEvent<S>>> getPublisher() {
		Lock lock = container.readLock();
		lock.lock();
		try {
			return publisher;
		} finally {
			lock.unlock();
		}
	}

	public ServiceContainer<ServiceHolder<S>> getContainer() {
		return container;
	}

	@Override
	public Elements<S> getSource() {
		Lock lock = container.readLock();
		lock.lock();
		try {
			Elements<S> firstSingletonElements = first == null ? Elements.empty() : Elements.singleton(first);
			Elements<S> lastSingletonElements = last == null ? Elements.empty() : Elements.singleton(last);
			return new MergedElements<>(firstSingletonElements, container.map((e) -> e.getSource()),
					lastSingletonElements);
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Registration inject(S service) {
		return injectors.inject(service);
	}

	@Override
	public boolean isEmpty() {
		return getSource().isEmpty();
	}

	protected Receipt onServiceInjectorEvents(Elements<ChangeEvent<ServiceInjector<? super S>>> events) {
		for (ChangeEvent<ServiceInjector<? super S>> event : events) {
			forEach((service) -> {
				if (event.getChangeType() != ChangeType.CREATE) {
					return;
				}

				event.getSource().inject(service);
			});
		}
		return Receipt.SUCCESS;
	}

	public Registration register(int order, S element) throws RegistrationException {
		return container.register(new ServiceHolder<>(order, element));
	}

	@Override
	public Registration register(S element) throws RegistrationException {
		return register(getDefaultOrder(), element);
	}

	@Override
	public Include<ServiceHolder<S>> registers(Elements<? extends S> elements) throws RegistrationException {
		return registers(getDefaultOrder(), elements);
	}

	public Include<ServiceHolder<S>> registers(int order, Elements<? extends S> elements) {
		Elements<ServiceHolder<S>> holders = elements.map((e) -> new ServiceHolder<>(order, e));
		return container.registers(holders);
	}

	@Override
	public void reload() {
		container.reload();
	}

	public void setComparator(Comparator<? super S> comparator) {
		container.setComparator((h1, h2) -> {
			int v = Integer.compare(h1.getOrder(), h2.getOrder());
			if (v == 0) {
				v = comparator.compare(h1.getSource(), h2.getSource());
				if (v == 0) {
					if (!ObjectUtils.equals(h1.getSource(), h2.getSource())) {
						v = 1;
					}
				}
			}
			return v;
		});
	}

	public void setDefaultOrder(int defaultOrder) {
		this.defaultOrder = defaultOrder;
	}

	public void setFirst(S first) {
		Lock lock = container.writeLock();
		lock.lock();
		try {
			ChangeEvent<S> event = new ChangeEvent<>(this.first, first);
			this.first = first;
			publisher.publish(Elements.singleton(event));
		} finally {
			lock.unlock();
		}
	}

	public void setLast(S last) {
		Lock lock = container.writeLock();
		lock.lock();
		try {
			ChangeEvent<S> event = new ChangeEvent<>(this.last, last);
			this.last = last;
			publisher.publish(Elements.singleton(event));
		} finally {
			lock.unlock();
		}
	}

	public void setPublisher(Publisher<? super Elements<ChangeEvent<S>>> publisher) {
		Lock lock = container.writeLock();
		lock.lock();
		try {
			this.publisher = publisher == null ? Publisher.empty() : publisher;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public String toString() {
		return getSource().toString();
	}

}
