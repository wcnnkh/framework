package io.basc.framework.util.observe.container;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.observe.Publisher;
import io.basc.framework.util.observe.event.ChangeEvent;
import io.basc.framework.util.observe.event.ChangeType;
import lombok.NonNull;

public class MapRegistry<K, V, M extends Map<K, EntryRegistration<K, V>>> extends EntryRegistry<K, V, M>
		implements Map<K, V> {
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
			return MapRegistry.this.size();
		}

	}

	private class InternalEntrySet extends AbstractSet<Entry<K, V>> {

		@Override
		public boolean add(Entry<K, V> e) {
			return addAll(Arrays.asList(e));
		}

		@Override
		public boolean addAll(Collection<? extends Entry<K, V>> c) {
			return MapRegistry.this.addAll(c);
		}

		@Override
		public Iterator<Entry<K, V>> iterator() {
			return read((map) -> map == null ? Collections.emptyIterator()
					: map.values().stream().map((e) -> (Entry<K, V>) e).collect(Collectors.toList()).iterator());
		}

		@Override
		public int size() {
			return MapRegistry.this.size();
		}

	}

	private class InternalCollection extends AbstractCollection<V> {

		@Override
		public Iterator<V> iterator() {
			return read((map) -> map == null ? Collections.emptyIterator()
					: map.values().stream().map((e) -> e.getValue()).collect(Collectors.toList()).iterator());
		}

		@Override
		public int size() {
			return MapRegistry.this.size();
		}
	}

	public MapRegistry(Supplier<? extends M> containerSupplier,
			@NonNull Publisher<? super Elements<ChangeEvent<KeyValue<K, V>>>> changeEventsPublisher) {
		super(containerSupplier, changeEventsPublisher);
	}

	public final boolean addAll(Collection<? extends Entry<? extends K, ? extends V>> c) {
		return !registers(Elements.of(c).map((e) -> KeyValue.of(e.getKey(), e.getValue()))).isCancelled();
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

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new InternalEntrySet();
	}

	@Override
	public V get(Object key) {
		return getValue((map) -> map.get(key));
	}

	@Override
	public boolean isEmpty() {
		return readAsBoolean((map) -> map == null ? true : map.isEmpty());
	}

	@Override
	public final Set<K> keySet() {
		return new InternalSet();
	}

	@Override
	public final V put(K key, V value) {
		KeyValue<K, V> keyValue = KeyValue.of(key, value);
		EntryRegistration<K, V> registration = newRegistration(keyValue);
		return write((map) -> {
			EntryRegistration<K, V> old = map.put(key, registration);
			if (old == null) {
				// created
				getChangeEventsPublisher().publish(Elements.singleton(new ChangeEvent<>(keyValue, ChangeType.CREATE)));
			} else {
				// update
				getChangeEventsPublisher().publish(Elements.singleton(new ChangeEvent<>(old, keyValue)));
			}
			return old == null ? null : old.getValue();
		});
	}

	@Override
	public final void putAll(Map<? extends K, ? extends V> m) {
		addAll(m.entrySet());
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
			getChangeEventsPublisher()
					.publish(Elements.singleton(new ChangeEvent<KeyValue<K, V>>(registration, ChangeType.DELETE)));
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
