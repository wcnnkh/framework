package io.basc.framework.util.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.function.BooleanSupplier;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Lifecycle;
import io.basc.framework.util.Listenable;
import io.basc.framework.util.Listener;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.Registration;
import io.basc.framework.util.Reloadable;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.actor.ChangeType;
import io.basc.framework.util.actor.EventDispatcher;
import io.basc.framework.util.register.LimitableRegistration;
import io.basc.framework.util.register.RegistrationException;
import io.basc.framework.util.register.container.AtomicElementRegistration;
import io.basc.framework.util.register.container.CollectionContainer;
import io.basc.framework.util.register.container.ElementRegistration;
import io.basc.framework.util.register.container.TreeSetContainer;
import lombok.RequiredArgsConstructor;

public class ServiceContainer<E> extends TreeSetContainer<E> implements ListenableServiceLoader<E> {
	private final EventDispatcher<Elements<ChangeEvent<E>>> eventDispatcher = new EventDispatcher<>();

	public ServiceContainer() {
		super.setPublisher(eventDispatcher);
	}

	public EventDispatcher<Elements<ChangeEvent<E>>> getEventDispatcher() {
		return eventDispatcher;
	}

	@Override
	public void setPublisher(Publisher<? super Elements<ChangeEvent<E>>> publisher) {
		super.setPublisher((events) -> {
			try {
				return publisher.publish(events);
			} finally {
				eventDispatcher.publish(events);
			}
		});
	}

	@RequiredArgsConstructor
	private class Include extends LimitableRegistration implements Reloadable {
		private AtomicBoolean initialized = new AtomicBoolean();
		private final Iterable<? extends E> iterable;
		private volatile Elements<ElementRegistration<E>> registrations;
		private final Reloadable reloadable;

		@Override
		public boolean cancel(BooleanSupplier cancel) {
			return super.cancel(() -> {
				Lock lock = getReadWriteLock().writeLock();
				lock.lock();
				try {
					if (registrations != null) {
						deregisters(registrations.filter((e) -> !e.isCancelled()).map((e) -> e.getPayload()));
						registrations = null;
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
			Lock lock = getReadWriteLock().writeLock();
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
				registrations = batchRegister(rightList).getElements();
			} else {
				List<E> leftList = new ArrayList<>();
				registrations.filter((e) -> !e.isCancelled()).map((e) -> e.getPayload()).forEach(leftList::add);
				Elements<ElementRegistration<E>> append = ServiceContainer.this.reload(leftList, rightList);
				this.registrations = this.registrations.concat(append).filter((e) -> !e.isCancelled()).toList();
			}
		}
	}

	private CollectionContainer<Include, Collection<ElementRegistration<Include>>> includes = new CollectionContainer<>(
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
			Lock lock = getReadWriteLock().writeLock();
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
	public Registration registers(Iterable<? extends E> elements) throws RegistrationException {
		if (elements instanceof Reloadable) {
			Reloadable reloadable = (Reloadable) elements;
			Include include = new Include(elements, reloadable);

			Registration registration = includes.register(include);
			// 初始化一下
			include.reload(false);
			return registration.and(include);
		}
		return super.registers(elements);
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
		deregisters(leftList);
		return batchRegister(rightList).getElements();
	}

	@Override
	public Registration registerListener(Listener<? super Elements<ChangeEvent<E>>> listener) {
		return eventDispatcher.registerListener(listener);
	}
}
