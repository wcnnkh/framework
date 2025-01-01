package io.basc.framework.util.register.container;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.ObjectUtils;
import io.basc.framework.util.exchange.Receipt;
import io.basc.framework.util.exchange.Registration;
import io.basc.framework.util.exchange.Registrations;
import io.basc.framework.util.exchange.event.ChangeEvent;
import io.basc.framework.util.exchange.event.ChangeType;
import io.basc.framework.util.register.KeyValueRegistry;
import io.basc.framework.util.register.RegistrationException;
import io.basc.framework.util.register.container.EntryRegistration.StandardEntryRegistrationWrapper;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
public class MapContainer<K, V, M extends Map<K, EntryRegistration<K, V>>> extends
		AbstractContainer<M, KeyValue<K, V>, EntryRegistration<K, V>> implements KeyValueRegistry<K, V>, Map<K, V> {
	@RequiredArgsConstructor
	private class BatchRegistrations implements Registrations<EntryRegistration<K, V>> {
		private final Elements<UpdateableEntryRegistration> registrations;

		@Override
		public boolean cancel() {
			Elements<UpdateableEntryRegistration> elements = this.registrations.filter((e) -> !e.isCancelled());
			// 全部设置为无效，防止调用默认的事件
			elements.forEach((e) -> e.getLimiter().limited());
			batchDeregister(elements);
			return true;
		}

		@Override
		public Elements<EntryRegistration<K, V>> getElements() {
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
			return MapContainer.this.size();
		}
	}

	private class InternalEntrySet extends AbstractSet<Entry<K, V>> {

		@Override
		public boolean add(Entry<K, V> e) {
			return addAll(Arrays.asList(e));
		}

		@Override
		public boolean addAll(Collection<? extends Entry<K, V>> c) {
			return MapContainer.this.addAll(c);
		}

		@Override
		public Iterator<Entry<K, V>> iterator() {
			return read((map) -> map == null ? Collections.emptyIterator()
					: map.values().stream().map((e) -> (Entry<K, V>) e).collect(Collectors.toList()).iterator());
		}

		@Override
		public int size() {
			return MapContainer.this.size();
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
			// 重写foreach那样可以不用进行内存拷贝
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
			return MapContainer.this.size();
		}

	}

	private class UpdateableEntryRegistration extends StandardEntryRegistrationWrapper<K, V, EntryRegistration<K, V>> {
		public UpdateableEntryRegistration(EntryRegistration<K, V> source) {
			super(source, Elements.empty());
		}

		private UpdateableEntryRegistration(
				StandardEntryRegistrationWrapper<K, V, EntryRegistration<K, V>> combinableServiceRegistration) {
			super(combinableServiceRegistration);
		}

		@Override
		public UpdateableEntryRegistration and(@NonNull Registration registration) {
			return new UpdateableEntryRegistration(super.and(registration));
		}

		@Override
		public boolean cancel(BooleanSupplier cancel) {
			return super.cancel(() -> {
				update((map) -> map == null ? null : map.remove(getKey(), getValue()));
				getPublisher().publish(Elements.singleton(new ChangeEvent<>(this, ChangeType.DELETE)));
				return true;
			});
		}

		@Override
		public V setValue(V value) {
			V oldValue = super.setValue(value);
			KeyValue<K, V> oldKeyValue = oldValue == null ? null : KeyValue.of(getKey(), oldValue);
			getPublisher().publish(Elements.singleton(new ChangeEvent<>(oldKeyValue, KeyValue.of(getKey(), value))));
			return oldValue;
		}
	}

	public MapContainer(Supplier<? extends M> containerSupplier) {
		super(containerSupplier);
	}

	public final boolean addAll(Collection<? extends Entry<? extends K, ? extends V>> c) {
		return !registers(Elements.of(c).map((e) -> KeyValue.of(e.getKey(), e.getValue()))).isCancelled();
	}

	protected final Receipt batchDeregister(Elements<? extends EntryRegistration<K, V>> registrations) {
		if (registrations.isEmpty()) {
			return Receipt.FAILURE;
		}

		registrations.forEach(Registration::cancel);
		execute((map) -> {
			registrations.forEach((e) -> map.remove(e.getKey(), e.getValue()));
			return true;
		});
		Elements<ChangeEvent<KeyValue<K, V>>> events = registrations
				.map((e) -> new ChangeEvent<>(e, ChangeType.DELETE));
		return getPublisher().publish(events);
	}

	public final Registrations<EntryRegistration<K, V>> batchRegister(Elements<? extends KeyValue<K, V>> elements)
			throws RegistrationException {
		Elements<ChangeInfo<K, V>> changes = write((map) -> {
			return elements.map((keyValue) -> {
				EntryRegistration<K, V> registration = newRegistration(keyValue);
				EntryRegistration<K, V> old = map.put(keyValue.getKey(), registration);
				ChangeEvent<KeyValue<K, V>> event;
				if (old == null) {
					// created
					event = new ChangeEvent<KeyValue<K, V>>(keyValue, ChangeType.CREATE);
				} else {
					// update
					event = new ChangeEvent<>(old, keyValue);
				}

				return new ChangeInfo<K, V>(event, registration);
				// 调用toList可以执行map中的内容
			}).toList();
		});
		getPublisher().publish(changes.map((e) -> e.event).toList());
		return convertToBatchRegistration(changes.map((e) -> e.registration).toList());
	}

	@Override
	public final void clear() {
		getRegistrations().cancel();
	}

	@Override
	public final boolean containsKey(Object key) {
		return readAsBoolean((map) -> map == null ? false : map.containsKey(key));
	}

	@Override
	public final boolean containsValue(Object value) {
		return readAsBoolean((map) -> map == null ? false : map.containsValue(value));
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
	public Receipt deregisterKeys(Iterable<? extends K> keys) {
		return update((map) -> {
			if (map == null) {
				return Receipt.FAILURE;
			}

			List<EntryRegistration<K, V>> removes = new ArrayList<>();
			for (K key : keys) {
				EntryRegistration<K, V> registration = map.remove(key);
				if (registration != null) {
					removes.add(registration);
				}
			}

			if (removes.isEmpty()) {
				return Receipt.FAILURE;
			}

			return batchDeregister(Elements.of(removes));
		});
	}

	@Override
	public Receipt deregisters(Elements<? extends KeyValue<K, V>> services) {
		return update((map) -> {
			if (map == null) {
				return Receipt.FAILURE;
			}

			List<EntryRegistration<K, V>> removes = new ArrayList<>();
			for (KeyValue<K, V> kv : services) {
				if (!map.containsKey(kv.getKey())) {
					continue;
				}

				EntryRegistration<K, V> registration = map.get(kv.getKey());
				if (registration == null || !ObjectUtils.equals(kv.getValue(), registration.getValue())) {
					continue;
				}

				registration = map.remove(kv.getKey());
				if (registration != null) {
					removes.add(registration);
				}
			}

			if (removes.isEmpty()) {
				return Receipt.FAILURE;
			}

			return batchDeregister(Elements.of(removes));
		});
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new InternalEntrySet();
	}

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
	public V get(Object key) {
		return getValue((map) -> map.get(key));
	}

	@Override
	public Elements<EntryRegistration<K, V>> getElements() {
		return getRegistrations().getElements();
	}

	public final Entry<K, V> getEntry(
			@NonNull Function<? super M, ? extends Entry<K, EntryRegistration<K, V>>> getter) {
		return read((map) -> {
			if (map == null) {
				return null;
			}

			Entry<K, EntryRegistration<K, V>> entry = getter.apply(map);
			if (entry == null) {
				return null;
			}

			return entry.getValue();
		});
	}

	public final EntryRegistration<K, V> getRegistration(
			@NonNull Function<? super M, ? extends EntryRegistration<K, V>> reader) {
		EntryRegistration<K, V> registration = read(reader);
		if (registration == null) {
			return null;
		}
		return new UpdateableEntryRegistration(registration);
	}

	public Registrations<EntryRegistration<K, V>> getRegistrations() {
		return getRegistrations((map) -> Elements.of(map.values()));
	}

	public final Registrations<EntryRegistration<K, V>> getRegistrations(
			Function<? super M, ? extends Elements<EntryRegistration<K, V>>> reader) {
		Elements<EntryRegistration<K, V>> registrations = readAsElements(
				(collection) -> collection == null ? Elements.empty() : reader.apply(collection));
		return convertToBatchRegistration(registrations);
	}

	@Override
	public Elements<KeyValue<K, V>> getKeyValues(K key) {
		return read((map) -> {
			if (map == null) {
				return Elements.empty();
			}

			if (!map.containsKey(key)) {
				return Elements.empty();
			}

			EntryRegistration<K, V> registration = map.get(key);
			if (registration == null) {
				return Elements.empty();
			}

			return Elements.singleton(registration);
		});
	}

	public final V getValue(Function<? super M, ? extends EntryRegistration<K, V>> getter) {
		return read((map) -> {
			if (map == null) {
				return null;
			}

			EntryRegistration<K, V> entryRegistration = getter.apply(map);
			return entryRegistration == null ? null : entryRegistration.getValue();
		});
	}

	@Override
	public boolean hasKey(K key) {
		return this.containsKey(key);
	}

	@Override
	public boolean isEmpty() {
		return readAsBoolean((map) -> map == null ? true : map.isEmpty());
	}

	@Override
	public Iterator<KeyValue<K, V>> iterator() {
		return read((map) -> map == null ? Collections.emptyIterator()
				: map.values().stream().map((e) -> (KeyValue<K, V>) e).collect(Collectors.toList()).iterator());
	}

	@Override
	public final Set<K> keySet() {
		return new InternalSet();
	}

	protected final AtomicEntryRegistration<K, V> newRegistration(KeyValue<K, V> keyValue) {
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
				getPublisher().publish(Elements.singleton(new ChangeEvent<>(keyValue, ChangeType.CREATE)));
			} else {
				// update
				getPublisher().publish(Elements.singleton(new ChangeEvent<>(old, keyValue)));
			}

			return old == null ? null : old.getValue();
		});
	}

	@Override
	public final void putAll(Map<? extends K, ? extends V> m) {
		addAll(m.entrySet());
	}

	@Override
	public Registration registers(Elements<? extends KeyValue<K, V>> elements) throws RegistrationException {
		return batchRegister(elements);
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

			registration.cancel();
			getPublisher()
					.publish(Elements.singleton(new ChangeEvent<KeyValue<K, V>>(registration, ChangeType.DELETE)));
			return registration.getValue();
		});
	}

	@Override
	public final int size() {
		return readInt((map) -> map == null ? 0 : map.size());
	}
	
	@Override
	public Stream<KeyValue<K, V>> stream() {
		return readAsElements(
				(map) -> map == null ? Elements.empty() : Elements.of(map.values()).map((e) -> (KeyValue<K, V>) e))
				.stream();
	}

	@Override
	public final Collection<V> values() {
		return new InternalCollection();
	}
}
