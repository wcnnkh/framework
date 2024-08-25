package io.basc.framework.util.register.container;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.util.KeyValue;
import io.basc.framework.util.Streams;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.event.EventPublishService;
import io.basc.framework.util.match.Matcher;
import io.basc.framework.util.observe.ChangeEvent;
import io.basc.framework.util.observe.ChangeType;
import io.basc.framework.util.register.KeyValueRegistry;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.RegistrationException;
import io.basc.framework.util.register.Registrations;
import io.basc.framework.util.register.empty.EmptyRegistrations;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class EntryRegistry<K, V, M extends Map<K, EntryRegistration<K, V>>> extends LazyContainer<M>
		implements KeyValueRegistry<K, V, EntryRegistration<K, V>>, Map<K, V>, Registration {
	@RequiredArgsConstructor
	private class BatchRegistrations implements Registrations<EntryRegistration<K, V>> {
		private final Elements<UpdateableEntryRegistration> registrations;

		@Override
		public void deregister() throws RegistrationException {
			Elements<UpdateableEntryRegistration> elements = this.registrations.filter((e) -> !e.isInvalid());
			// 全部设置为无效，防止调用默认的事件
			elements.forEach((e) -> e.getLimiter().limited());
			batchDeregister(elements);
		}

		@Override
		public Elements<EntryRegistration<K, V>> getRegistrations() {
			return registrations.map((e) -> e);
		}
	}

	@RequiredArgsConstructor
	private static class ChangeInfo<K, V> {
		private final ChangeEvent<KeyValue<K, V>> event;
		private final EntryRegistration<K, V> registration;
	}

	private class InternalCollection extends AbstractCollection<V> {

		@Override
		public Iterator<V> iterator() {
			return read((map) -> map == null ? Collections.emptyIterator()
					: map.values().stream().map((e) -> e.getValue()).collect(Collectors.toList()).iterator());
		}

		@Override
		public int size() {
			return EntryRegistry.this.size();
		}

	}

	private class InternalElements implements Elements<KeyValue<K, V>> {

		@Override
		public void forEach(Consumer<? super KeyValue<K, V>> action) {
			read((map) -> {
				if (map == null) {
					return null;
				}

				map.entrySet().stream().map((e) -> e.getValue()).forEach(action);
				return null;
			});
		}

		@Override
		public Iterator<KeyValue<K, V>> iterator() {
			return read((map) -> map == null ? Collections.emptyIterator()
					: map.values().stream().map((e) -> (KeyValue<K, V>) e).collect(Collectors.toList()).iterator());
		}

		@Override
		public Stream<KeyValue<K, V>> stream() {
			// copy一下来保证线程安全
			return read((map) -> map == null ? Streams.empty()
					: map.values().stream().map((e) -> (KeyValue<K, V>) e).collect(Collectors.toList()).stream());
		}
	}

	private class InternalEntrySet extends AbstractSet<Entry<K, V>> {

		@Override
		public boolean add(Entry<K, V> e) {
			return addAll(Arrays.asList(e));
		}

		@Override
		public boolean addAll(Collection<? extends Entry<K, V>> c) {
			return EntryRegistry.this.addAll(c);
		}

		@Override
		public Iterator<Entry<K, V>> iterator() {
			return read((map) -> map == null ? Collections.emptyIterator()
					: map.values().stream().map((e) -> (Entry<K, V>) e).collect(Collectors.toList()).iterator());
		}

		@Override
		public int size() {
			return EntryRegistry.this.size();
		}

	}

	private class InternalSet extends AbstractSet<K> {

		@Override
		public boolean add(K e) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(Collection<? extends K> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void forEach(Consumer<? super K> action) {
			// 重写foreach那样可以不用copy节约内存
			read((map) -> {
				if (map == null) {
					return null;
				}
				map.keySet().forEach(action);
				return null;
			});
		}

		@Override
		public Iterator<K> iterator() {
			// 使用copy保证线程安全
			return read((map) -> map == null ? Collections.emptyIterator()
					: map.keySet().stream().collect(Collectors.toList()).iterator());
		}

		@Override
		public int size() {
			return EntryRegistry.this.size();
		}

	}

	private class UpdateableEntryRegistration extends CombinableEntryRegistration<K, V, EntryRegistration<K, V>> {
		private UpdateableEntryRegistration(
				CombinableEntryRegistration<K, V, EntryRegistration<K, V>> combinableServiceRegistration) {
			super(combinableServiceRegistration);
		}

		public UpdateableEntryRegistration(EntryRegistration<K, V> source) {
			super(source, Registration.EMPTY);
		}

		@Override
		public UpdateableEntryRegistration and(@NonNull Registration registration) {
			return new UpdateableEntryRegistration(super.and(registration));
		}

		@Override
		public void deregister(Runnable runnable) throws RegistrationException {
			super.deregister(() -> {
				try {
					runnable.run();
				} finally {
					update((map) -> map == null ? null : map.remove(getKey(), source));
					eventPublishService.publishEvent(new ChangeEvent<>(this, ChangeType.DELETE));
				}
			});
		}

		@Override
		public V setValue(V value) {
			V oldValue = super.setValue(value);
			eventPublishService
					.publishEvent(new ChangeEvent<>(KeyValue.of(getKey(), value), KeyValue.of(getKey(), oldValue)));
			return oldValue;
		}
	}

	@NonNull
	private final EventPublishService<ChangeEvent<KeyValue<K, V>>> eventPublishService;

	public EntryRegistry(Supplier<? extends M> containerSupplier,
			@NonNull EventPublishService<ChangeEvent<KeyValue<K, V>>> eventPublishService) {
		super(containerSupplier);
		this.eventPublishService = eventPublishService;
	}

	@Override
	public Elements<KeyValue<K, V>> getElements(K key, Matcher<? super K> matcher) {
		return read((map) -> {
			if (map == null) {
				return Elements.empty();
			}

			if (matcher.isPattern(key)) {
				return Elements.of(map.values().stream().filter((e) -> matcher.match(key, e.getKey()))
						.collect(Collectors.toList()));
			} else {
				EntryRegistration<K, V> registration = map.get(key);
				return registration == null ? Elements.empty() : Elements.singleton(registration);
			}
		});
	}

	public final boolean addAll(Collection<? extends Entry<? extends K, ? extends V>> c) {
		return !registers(Elements.of(c).map((e) -> KeyValue.of(e.getKey(), e.getValue()))).isInvalid();
	}

	private boolean batchDeregister(Elements<? extends EntryRegistration<K, V>> registrations) {
		if (registrations.isEmpty()) {
			return false;
		}

		registrations.forEach(Registration::deregister);
		execute((map) -> {
			registrations.forEach((e) -> map.remove(e.getKey(), e.getValue()));
			return true;
		});
		Elements<ChangeEvent<KeyValue<K, V>>> events = registrations
				.map((e) -> new ChangeEvent<>(e, ChangeType.DELETE));
		eventPublishService.publishBatchEvents(events);
		return true;
	}

	@Override
	public final void clear() {
		execute((map) -> {
			Elements<EntryRegistration<K, V>> registrations = Elements.of(map.values());
			batchDeregister(registrations);
			return true;
		});
	}

	@Override
	public final boolean isInvalid() {
		return isEmpty();
	}

	@Override
	public final void deregister() throws RegistrationException {
		clear();
	}

	@Override
	public final boolean containsKey(Object key) {
		return test((map) -> map == null ? false : map.containsKey(key));
	}

	@Override
	public final boolean containsValue(Object value) {
		return test((map) -> map == null ? false : map.containsValue(value));
	}

	private final Registrations<EntryRegistration<K, V>> convertToBatchRegistration(
			Elements<EntryRegistration<K, V>> registrations) {
		if (registrations == null || registrations.isEmpty()) {
			return EmptyRegistrations.empty();
		}

		Elements<UpdateableEntryRegistration> updateableRegistrations = registrations
				.map((e) -> new UpdateableEntryRegistration(e));
		return new BatchRegistrations(updateableRegistrations);
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new InternalEntrySet();
	}

	@Override
	public V get(Object key) {
		EntryRegistration<K, V> entryRegistration = read((map) -> map == null ? null : map.get(key));
		return entryRegistration == null ? null : entryRegistration.getValue();
	}

	public final EntryRegistration<K, V> getRegistration(
			@NonNull Function<? super M, ? extends EntryRegistration<K, V>> reader) {
		EntryRegistration<K, V> registration = read(reader);
		if (registration == null) {
			return null;
		}
		return new UpdateableEntryRegistration(registration);
	}

	public final Registrations<EntryRegistration<K, V>> getRegistrations(
			Function<? super M, ? extends Elements<EntryRegistration<K, V>>> reader) {
		Elements<EntryRegistration<K, V>> registrations = read((collection) -> reader.apply(collection));
		return convertToBatchRegistration(registrations);
	}

	@Override
	public final Elements<KeyValue<K, V>> getServices() {
		return new InternalElements();
	}

	@Override
	public final boolean isEmpty() {
		return test((map) -> map == null ? true : map.isEmpty());
	}

	@Override
	public final Set<K> keySet() {
		return new InternalSet();
	}

	private EntryRegistration<K, V> newRegistration(KeyValue<K, V> keyValue) {
		return new AtomicEntryRegistration<>(keyValue.getKey(), keyValue.getValue());
	}

	@Override
	public final V put(K key, V value) {
		KeyValue<K, V> keyValue = KeyValue.of(key, value);
		EntryRegistration<K, V> registration = newRegistration(keyValue);
		return write((map) -> {
			EntryRegistration<K, V> old = map.put(key, registration);
			if (old == null) {
				// created
				eventPublishService.publishEvent(new ChangeEvent<>(keyValue, ChangeType.CREATE));
			} else {
				// update
				eventPublishService.publishEvent(new ChangeEvent<>(keyValue, old));
			}
			return old == null ? null : old.getValue();
		});
	}

	@Override
	public final void putAll(Map<? extends K, ? extends V> m) {
		addAll(m.entrySet());
	}

	@Override
	public final EntryRegistration<K, V> register(KeyValue<K, V> element) throws RegistrationException {
		return registers(Arrays.asList(element)).getRegistrations().first();
	}

	@Override
	public final Registrations<EntryRegistration<K, V>> registers(Iterable<? extends KeyValue<K, V>> elements)
			throws RegistrationException {
		Elements<ChangeInfo<K, V>> changes = write((map) -> {
			return Elements.of(elements).map((keyValue) -> {
				EntryRegistration<K, V> registration = newRegistration(keyValue);
				EntryRegistration<K, V> old = map.put(keyValue.getKey(), registration);
				ChangeEvent<KeyValue<K, V>> event;
				if (old == null) {
					// created
					event = new ChangeEvent<KeyValue<K, V>>(keyValue, ChangeType.CREATE);
				} else {
					// update
					event = new ChangeEvent<>(keyValue, old);
				}
				return new ChangeInfo<K, V>(event, registration);
				// 调用toList可以执行map中的内容
			}).toList();
		});
		eventPublishService.publishBatchEvents(changes.map((e) -> e.event).toList());
		return convertToBatchRegistration(changes.map((e) -> e.registration).toList());
	}

	// 默认推送一下所有的更新事件，可以重写此方法关闭
	@Override
	public void reload() {
		read((map) -> {
			if (map == null) {
				return null;
			}

			List<ChangeEvent<KeyValue<K, V>>> events = map.entrySet().stream()
					.map((e) -> new ChangeEvent<>(KeyValue.of(e.getKey(), e.getValue().getValue()), ChangeType.UPDATE))
					.collect(Collectors.toList());
			eventPublishService.publishBatchEvents(Elements.of(events));
			return null;
		});
	}

	@Override
	public final V remove(Object key) {
		return update((map) -> {
			if (map == null) {
				return null;
			}

			EntryRegistration<K, V> registration = map.remove(key);
			if (registration == null) {
				return null;
			}

			registration.deregister();
			eventPublishService.publishEvent(new ChangeEvent<KeyValue<K, V>>(registration, ChangeType.DELETE));
			return registration.getValue();
		});
	}

	@Override
	public final int size() {
		return readInt((map) -> map == null ? 0 : map.size());
	}

	@Override
	public final Collection<V> values() {
		return new InternalCollection();
	}
}
