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
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.Observable;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.Registration;
import io.basc.framework.util.Reloadable;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.register.LimitableRegistration;
import io.basc.framework.util.register.RegistrationException;
import io.basc.framework.util.register.container.ElementRegistration;
import io.basc.framework.util.register.container.ElementRegistry;
import io.basc.framework.util.register.container.TreeSetRegistry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class Services<S> extends TreeSetRegistry<S> implements ServiceLoader<S> {

	@RequiredArgsConstructor
	private class Include extends LimitableRegistration implements Reloadable {
		private AtomicBoolean initialized = new AtomicBoolean();
		private final Iterable<? extends S> iterable;
		private volatile Elements<ElementRegistration<S>> registrations;
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

			List<S> rightList = new ArrayList<>();
			iterable.forEach(rightList::add);

			if (registrations == null) {
				registrations = batchRegister(rightList).getElements();
			} else {
				List<S> leftList = new ArrayList<>();
				registrations.filter((e) -> !e.isCancelled()).map((e) -> e.getPayload()).forEach(leftList::add);
				Elements<ElementRegistration<S>> append = Services.this.reload(leftList, rightList);
				this.registrations = this.registrations.concat(append).filter((e) -> !e.isCancelled()).toList();
			}
		}
	}

	private ElementRegistry<Include, Collection<ElementRegistration<Include>>> includes = new ElementRegistry<>(
			LinkedHashSet::new, Publisher.empty());

	public Services(@NonNull Publisher<? super Elements<ChangeEvent<S>>> changeEventsPublisher) {
		super(changeEventsPublisher);
	}

	protected boolean equals(S left, S right) {
		Comparator<? super S> comparator = this.comparator();
		if (comparator == null) {
			return ObjectUtils.equals(left, right);
		} else {
			return comparator.compare(left, right) == 0;
		}
	}

	@Override
	public Registration registers(Iterable<? extends S> elements) throws RegistrationException {
		if (elements instanceof Reloadable) {
			Reloadable reloadable = (Reloadable) elements;
			Include include = new Include(elements, reloadable);

			Registration registration = includes.register(include);
			// 初始化一下
			include.reload(false);
			return registration.and(include);
		} else if (elements instanceof Observable) {
			Include include = new Include(elements, null);
			// 先注册事件再初始化
			Registration registration = includes.register(include);
			Observable<?> observable = (Observable<?>) elements;
			registration = registration.and(observable.registerListener((events) -> include.reload()));
			// 初始化一下
			include.reload(false);
			return registration.and(include);
		}
		return super.registers(elements);
	}

	@Override
	public void reload() {
		includes.getElements().forEach(Reloadable::reload);
	}

	private Elements<ElementRegistration<S>> reload(List<S> leftList, List<S> rightList) {
		Iterator<S> leftIterator = leftList.iterator();
		while (leftIterator.hasNext()) {
			S left = leftIterator.next();
			Iterator<S> rightIterator = rightList.iterator();
			while (rightIterator.hasNext()) {
				S right = rightIterator.next();
				if (Services.this.equals(left, right)) {
					leftIterator.remove();
					rightIterator.remove();
				}
			}
		}

		// 左边剩下的说明被删除了
		deregisters(leftList);
		return batchRegister(rightList).getElements();
	}
}
