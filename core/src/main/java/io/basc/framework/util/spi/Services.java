package io.basc.framework.util.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Lifecycle;
import io.basc.framework.util.Listener;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.Observable;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.Registration;
import io.basc.framework.util.Reloadable;
import io.basc.framework.util.ServiceLoader;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.actor.ChangeType;
import io.basc.framework.util.register.LimitableRegistration;
import io.basc.framework.util.register.RegistrationException;
import io.basc.framework.util.register.container.AtomicElementRegistration;
import io.basc.framework.util.register.container.ElementRegistration;
import io.basc.framework.util.register.container.ElementRegistry;
import io.basc.framework.util.register.container.TreeSetRegistry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class Services<S> extends TreeSetRegistry<S> implements ServiceLoader<S> {

	@RequiredArgsConstructor
	private class ForwardListener implements Listener<Lifecycle> {
		@NonNull
		private final Observable<?> observable;
		private volatile Registration registration;

		@Override
		public void accept(Lifecycle source) {
			synchronized (this) {
				if (source.isRunning()) {
					if (registration == null) {
						registration = observable.registerListener((event) -> {
							@SuppressWarnings("unchecked")
							ChangeEvent<S> changeEvent = new ChangeEvent<>((S) observable, ChangeType.UPDATE);
							getChangeEventsPublisher().publish(Elements.singleton(changeEvent));
						});
					}
				} else {
					if (registration != null) {
						registration.cancel();
					}
				}
			}
		}
	}

	@RequiredArgsConstructor
	private class Include extends LimitableRegistration implements Reloadable {
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
			Lock lock = getReadWriteLock().writeLock();
			lock.lock();
			try {
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
			} finally {
				lock.unlock();
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
	protected AtomicElementRegistration<S> newElementRegistration(S element) {
		AtomicElementRegistration<S> registration = super.newElementRegistration(element);
		if (element instanceof Observable) {
			Observable<?> observable = (Observable<?>) element;
			registration.registerListener(new ForwardListener(observable));
		}
		return registration;
	}

	@Override
	public final Registration registers(Iterable<? extends S> elements) throws RegistrationException {
		if (elements instanceof ObservableServiceLoader) {
			Registration registration = super.registers(elements);
			ObservableServiceLoader<? extends S> observableServiceLoader = (ObservableServiceLoader<? extends S>) elements;
			Registration internalRegistration = observableServiceLoader.registerListener(this::touchInternalChange);
			return registration.and(internalRegistration);
		} else if (elements instanceof Reloadable) {
			Reloadable reloadable = (Reloadable) elements;
			Include include = new Include(elements, reloadable);

			Registration registration = includes.register(include);
			// 初始化一下
			include.reload();
			return registration.and(include);
		} else if (elements instanceof Observable) {
			Include include = new Include(elements, null);
			Registration registration = includes.register(include);
			// 初始化一下
			include.reload();
			registration = registration.and(include);

			Observable<?> observable = (Observable<?>) elements;
			registration = registration.and(observable.registerListener((events) -> include.reload()));
			return registration;
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

	private void touchInternalChange(Elements<? extends ChangeEvent<? extends S>> events) {
		// 想办法实现批量操作
		for (ChangeEvent<? extends S> event : events) {
			if (event.getChangeType() == ChangeType.DELETE) {
				deregister(event.getSource());
			} else if (event.getChangeType() == ChangeType.CREATE) {
				register(event.getSource());
			} else if (event.getChangeType() == ChangeType.UPDATE) {
				if (event.getUpdatedSource() == null) {
					// 纯更新操作只推送事件
					getChangeEventsPublisher().publish(Elements.singleton(event.convert(Function.identity())));
				} else {
					// 使用先删除后添加来代替事件
					deregister(event.getUpdatedSource());
					register(event.getSource());
				}
			}
		}
	}
}
