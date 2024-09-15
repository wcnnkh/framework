package io.basc.framework.util.dict;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.Lifecycle;
import io.basc.framework.util.Listener;
import io.basc.framework.util.NoUniqueElementException;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.Receipt;
import io.basc.framework.util.Registration;
import io.basc.framework.util.collect.SetWrapper;
import io.basc.framework.util.event.ChangeEvent;
import io.basc.framework.util.register.container.AtomicElementRegistration;
import io.basc.framework.util.register.container.ElementRegistration;
import io.basc.framework.util.register.container.TreeSetRegistry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Dictionarys<K, V, T extends Dictionary<K, V>> implements SetWrapper<T, TreeSetRegistry<T>> {
	private class DictionarysRegistry extends TreeSetRegistry<T> {

		public DictionarysRegistry() {
			super(Dictionarys.this::onDictionaryChange);
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

	private class DictionaryView implements Dictionary<K, V> {

		@Override
		public Elements<KeyValue<K, V>> getElements() {
			return source.getElements().flatMap((e) -> e.getElements()).distinct();
		}

		@Override
		public Elements<KeyValue<K, V>> getElements(K key) {
			return source.getElements().flatMap((e) -> e.getElements(key)).distinct();
		}

		@Override
		public V getUniqueValue(K key) throws NoSuchElementException, NoUniqueElementException {
			return source.read((set) -> {
				if (set == null) {
					throw new NoSuchElementException(key + "");
				}

				for (ElementRegistration<T> dict : set) {
					if (dict.getPayload().hasUniqueValue(key)) {
						return dict.getPayload().getUniqueValue(key);
					}
				}
				throw new NoSuchElementException(key + "");
			});
		}

		@Override
		public boolean hasKey(K key) {
			return source.readAsBoolean((set) -> set.stream().anyMatch((e) -> e.getPayload().hasKey(key)));
		}

		@Override
		public boolean hasUniqueValue(K key) {
			return source.readAsBoolean((set) -> set.stream().anyMatch((e) -> e.getPayload().hasUniqueValue(key)));
		}

		@Override
		public boolean isEmpty() {
			return source.isEmpty()
					|| source.readAsBoolean((set) -> set.stream().allMatch((e) -> e.getPayload().isEmpty()));
		}

		@Override
		public Elements<K> keys() {
			return source.getElements().flatMap((e) -> e.keys()).distinct();
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
	@NonNull
	private final Publisher<? super Elements<ChangeEvent<K>>> keyEventsPublisher;
	private final Dictionary<K, V> merged = new DictionaryView();

	@NonNull
	private final Predicate<? super T> observableKeysPredicate;

	private final TreeSetRegistry<T> source = new DictionarysRegistry();

	@Override
	public TreeSetRegistry<T> getSource() {
		return source;
	}

	public Dictionary<K, V> merged() {
		return merged;
	}

	private Receipt onDictionaryChange(Elements<ChangeEvent<T>> events) {
		Lock lock = source.getReadWriteLock().writeLock();
		lock.lock();
		try {
			Elements<ChangeEvent<T>> sourceEvents = events;
			if (source.comparator() != null) {
				sourceEvents = sourceEvents
						.sorted((o1, o2) -> source.comparator().compare(o1.getSource(), o2.getSource()));
			}

			Collection<ChangeEvent<K>> targetEvents = sourceEvents.flatMap((event) -> {
				T dict = event.getSource();
				Set<T> headSet = source.headSet(dict);
				return dict.getElements().map((kv) -> {
					if (headSet.stream().anyMatch((e) -> e.hasKey(kv.getKey()))) {
						// 如果上级存在就忽略
						return null;
					}
					return new ChangeEvent<>(kv.getKey(), event.getChangeType());
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
}
