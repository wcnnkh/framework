package io.basc.framework.util.dict;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Keys;
import io.basc.framework.util.Lifecycle;
import io.basc.framework.util.Listener;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.Receipt;
import io.basc.framework.util.Registration;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.register.container.AtomicElementRegistration;
import io.basc.framework.util.register.container.TreeSetRegistry;
import io.basc.framework.util.spi.ObservableKeys;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MergedKeys<K, T extends Keys<K>> implements Keys<K> {
	private class Includes extends TreeSetRegistry<T> {

		public Includes() {
			super(MergedKeys.this::onIncludesChange);
		}

		@Override
		protected AtomicElementRegistration<T> newElementRegistration(T element) {
			AtomicElementRegistration<T> registration = super.newElementRegistration(element);
			if (observableKeysPredicate.test(element)) {
				// 如果是一个ObservableKeys
				@SuppressWarnings("unchecked")
				ObservableKeys<K> observableKeys = (ObservableKeys<K>) element;
				registration.registerListener(new ObservableKeysForwardListener(observableKeys));
			}
			return registration;
		}
	}

	@RequiredArgsConstructor
	private class ObservableKeysForwardListener implements Listener<Lifecycle> {
		@NonNull
		private final ObservableKeys<K> observableKeys;
		private volatile Registration registration;

		@Override
		public void accept(Lifecycle source) {
			synchronized (this) {
				if (source.isRunning()) {
					if (registration == null) {
						registration = observableKeys.registerListener(keyEventsPublisher::publish);
					}
				} else {
					if (registration != null) {
						registration.cancel();
					}
				}
			}
		}
	}

	private final TreeSetRegistry<T> includes = new Includes();

	@NonNull
	private final Publisher<? super Elements<ChangeEvent<K>>> keyEventsPublisher;

	@NonNull
	private final Predicate<? super T> observableKeysPredicate;

	@Override
	public Elements<K> fetchKeys() {
		return includes.getElements().flatMap((e) -> e.fetchKeys()).distinct();
	}

	private Receipt onIncludesChange(Elements<ChangeEvent<T>> events) {
		Lock lock = includes.getReadWriteLock().writeLock();
		lock.lock();
		try {
			Elements<ChangeEvent<T>> sourceEvents = events;
			if (includes.comparator() != null) {
				sourceEvents = sourceEvents
						.sorted((o1, o2) -> includes.comparator().compare(o1.getSource(), o2.getSource()));
			}

			Collection<ChangeEvent<K>> targetEvents = sourceEvents.flatMap((event) -> {
				T dict = event.getSource();
				Set<T> headSet = includes.headSet(dict);
				return dict.fetchKeys().map((k) -> {
					if (ignoreIncludesEvent(headSet, k)) {
						return null;
					}
					return new ChangeEvent<>(k, event.getChangeType());
				});
			}).filter((e) -> e != null)
					// 二次去重
					.collect(Collectors.toMap((e) -> e.getSource(), (e) -> e, (a, b) -> b, LinkedHashMap::new))
					.values();
			return keyEventsPublisher.publish(Elements.of(targetEvents));
		} finally {
			lock.unlock();
		}
	}

	protected boolean ignoreIncludesEvent(Set<T> parents, K key) {
		// 如果上级存在就忽略
		return parents.stream().anyMatch((e) -> e.hasKey(key));
	}

	public TreeSetRegistry<T> includes() {
		return includes;
	}
}
