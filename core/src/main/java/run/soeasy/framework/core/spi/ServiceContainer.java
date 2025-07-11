package run.soeasy.framework.core.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.function.BooleanSupplier;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.Provider;
import run.soeasy.framework.core.collection.Reloadable;
import run.soeasy.framework.core.exchange.Lifecycle;
import run.soeasy.framework.core.exchange.Listenable;
import run.soeasy.framework.core.exchange.Listener;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.container.AtomicElementRegistration;
import run.soeasy.framework.core.exchange.container.ElementRegistration;
import run.soeasy.framework.core.exchange.container.LimitableRegistration;
import run.soeasy.framework.core.exchange.container.RegistrationException;
import run.soeasy.framework.core.exchange.container.collection.CollectionContainer;
import run.soeasy.framework.core.exchange.container.map.TreeSetContainer;
import run.soeasy.framework.core.exchange.event.ChangeEvent;
import run.soeasy.framework.core.exchange.event.ChangeType;

public class ServiceContainer<E> extends TreeSetContainer<E> implements Provider<E> {

	public ServiceContainer() {
		setComparator(ServiceComparator.defaultServiceComparator());
	}

	@RequiredArgsConstructor
	@Getter
	@Setter
	private class InternalInclude extends LimitableRegistration implements Include<E> {
		private AtomicBoolean initialized = new AtomicBoolean();
		private final Iterable<? extends E> iterable;
		private volatile Elements<ElementRegistration<E>> registrations;
		private final Reloadable reloadable;
		private Registration registration;

		@Override
		public boolean cancel(BooleanSupplier cancel) {
			return super.cancel(() -> {
				Lock lock = writeLock();
				lock.lock();
				try {
					if (registrations != null) {
						deregisters(registrations.filter((e) -> !e.isCancelled()).map((e) -> e.getPayload()));
						registrations = null;
					}

					if (registration != null) {
						registration.cancel();
					}
				} finally {
					lock.unlock();
				}
				return true;
			});
		}

		@Override
		public void reload() {
			reload(true);
		}

		public void reload(boolean force) {
			Lock lock = writeLock();
			lock.lock();
			try {
				if (initialized.compareAndSet(false, true) || force) {
					update();
				}
			} finally {
				lock.unlock();
			}
		}

		private void update() {
			if (reloadable != null) {
				reloadable.reload();
			}

			List<E> rightList = new ArrayList<>();
			iterable.forEach(rightList::add);

			if (registrations == null) {
				registrations = batchRegister(Elements.of(rightList)).getElements();
			} else {
				List<E> leftList = new ArrayList<>();
				registrations.filter((e) -> !e.isCancelled()).map((e) -> e.getPayload()).forEach(leftList::add);
				Elements<ElementRegistration<E>> append = ServiceContainer.this.reload(leftList, rightList);
				this.registrations = this.registrations.concat(append).filter((e) -> !e.isCancelled()).toList();
			}
		}

		@Override
		public Iterator<E> iterator() {
			return registrations == null ? Collections.emptyIterator()
					: registrations.filter((e) -> !e.isCancelled()).map((e) -> e.getPayload()).iterator();
		}
	}

	private CollectionContainer<Include<E>, Collection<ElementRegistration<Include<E>>>> includes = new CollectionContainer<>(
			LinkedHashSet::new);

	protected boolean equals(E left, E right) {
		Comparator<? super E> comparator = getComparator();
		if (comparator == null) {
			return ObjectUtils.equals(left, right);
		} else {
			return comparator.compare(left, right) == 0;
		}
	}

	@RequiredArgsConstructor
	private class InternalObserver implements Listener<Lifecycle> {
		private final E element;
		private final Listenable<?> observable;
		private volatile Registration registration;

		@Override
		public void accept(Lifecycle source) {
			Lock lock = writeLock();
			lock.lock();
			try {
				if (source.isRunning()) {
					if (registration != null) {
						registration.cancel();
						registration = null;
					}

					registration = observable.registerListener((e) -> getPublisher()
							.publish(Elements.singleton(new ChangeEvent<>(element, ChangeType.UPDATE))));
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
	protected AtomicElementRegistration<E> newElementRegistration(E element) {
		AtomicElementRegistration<E> registration = super.newElementRegistration(element);
		if (element instanceof Listenable) {
			registration.registerListener(new InternalObserver(element, (Listenable<?>) element));
		}
		return registration;
	}

	@Override
	public Include<E> registers(Elements<? extends E> elements) throws RegistrationException {
		Reloadable reloadable = null;
		if (elements instanceof Reloadable) {
			reloadable = (Reloadable) elements;
		}
		InternalInclude include = new InternalInclude(elements, reloadable);
		Registration registration = includes.register(include);
		// 初始化一下
		include.reload(false);
		include.setRegistration(registration);
		return include;
	}

	@Override
	public void reload() {
		includes.forEach((e) -> e.reload());
	}

	private Elements<ElementRegistration<E>> reload(List<E> leftList, List<E> rightList) {
		Iterator<E> leftIterator = leftList.iterator();
		while (leftIterator.hasNext()) {
			E left = leftIterator.next();
			Iterator<E> rightIterator = rightList.iterator();
			while (rightIterator.hasNext()) {
				E right = rightIterator.next();
				if (ServiceContainer.this.equals(left, right)) {
					leftIterator.remove();
					rightIterator.remove();
				}
			}
		}

		// 左边剩下的说明被删除了
		deregisters(Elements.of(leftList));
		return batchRegister(Elements.of(rightList)).getElements();
	}
}
