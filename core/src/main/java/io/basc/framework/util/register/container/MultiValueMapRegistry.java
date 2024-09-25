package io.basc.framework.util.register.container;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.collect.MultiValueMap;
import io.basc.framework.util.concurrent.ReadOnlyEntry;
import lombok.NonNull;

public class MultiValueMapRegistry<K, V, L extends Collection<ElementRegistration<V>>, M extends Map<K, ElementRegistry<V, L>>>
		extends MultiValueRegistry<K, V, L, M> implements MultiValueMap<K, V> {

	public MultiValueMapRegistry(@NonNull Supplier<? extends M> containerSupplier,
			@NonNull Supplier<? extends L> valuesSupplier,
			@NonNull Publisher<? super Elements<ChangeEvent<KeyValue<K, V>>>> changeEventsPublisher) {
		super(containerSupplier, valuesSupplier, changeEventsPublisher);
	}

	@Override
	public final int size() {
		return readInt((map) -> map == null ? 0 : map.size());
	}

	@Override
	public final boolean containsKey(Object key) {
		return readAsBoolean((map) -> map == null ? false : map.containsKey(key));
	}

	@Override
	public final boolean containsValue(Object value) {
		return readAsBoolean((map) -> {
			if (map == null) {
				return false;
			}

			return map.containsKey(value);
		});
	}

	@Override
	public final List<V> get(Object key) {
		ElementRegistry<V, L> registry = getElementRegistry(key);
		return registry == null ? null : registry.getElements().toList();
	}

	@Override
	public final List<V> put(K key, List<V> value) {
		return write((map) -> {
			List<V> oldValues = null;
			ElementRegistry<V, L> values = map.get(key);
			if (values == null) {
				values = newValues(key);
				map.put(key, values);
			} else {
				oldValues = values.getElements().toList();
				// 清空所有
				values.clear();
			}

			values.registers(value);
			return oldValues;
		});
	}

	@Override
	public final List<V> remove(Object key) {
		return update((map) -> {
			if (map == null) {
				return null;
			}

			ElementRegistry<V, L> values = map.remove(key);
			if (values == null) {
				return null;
			}

			try {
				return values.getElements().toList();
			} finally {
				values.clear();
			}
		});
	}

	@Override
	public final void putAll(Map<? extends K, ? extends List<V>> m) {
		write((map) -> {
			for (Entry<? extends K, ? extends List<V>> entry : m.entrySet()) {
				ElementRegistry<V, L> values = map.get(entry.getKey());
				if (values == null) {
					values = newValues(entry.getKey());
					map.put(entry.getKey(), values);
				} else {
					// 清空所有
					values.clear();
				}
				values.registers(entry.getValue());
			}
			return null;
		});
	}

	@Override
	public final Set<K> keySet() {
		return read((map) -> map == null ? Collections.emptySet() : map.keySet());
	}

	@Override
	public final Collection<List<V>> values() {
		return read((map) -> map == null ? Collections.emptyList()
				: map.values().stream().map((e) -> e.getElements().toList()).collect(Collectors.toList()));
	}

	@Override
	public final Set<Entry<K, List<V>>> entrySet() {
		return read((map) -> map == null ? Collections.emptySet()
				: map.entrySet().stream()
						.map((e) -> new ReadOnlyEntry<>(e.getKey(), (List<V>) e.getValue().getElements().toList()))
						.collect(Collectors.toSet()));
	}

	@Override
	public final V getFirst(K key) {
		ElementRegistry<V, L> values = getElementRegistry(key);
		if (values == null) {
			return null;
		}

		return values.read((collection) -> {
			if (collection == null) {
				return null;
			}

			ElementRegistration<V> registration = CollectionUtils.first(collection);
			return registration == null ? null : registration.getPayload();
		});
	}

	@Override
	public final void add(K key, V value) {
		write((map) -> {
			ElementRegistry<V, L> values = map.get(key);
			if (values == null) {
				values = newValues(key);
				map.put(key, values);
			}
			values.register(value);
			return null;
		});
	}

	@Override
	public final void set(K key, V value) {
		write((map) -> {
			ElementRegistry<V, L> values = map.get(key);
			if (values == null) {
				values = newValues(key);
				map.put(key, values);
			} else {
				values.clear();
			}
			values.register(value);
			return null;
		});
	}

	@Override
	public final void setAll(Map<K, V> values) {
		if (CollectionUtils.isEmpty(values)) {
			return;
		}

		for (Entry<K, V> entry : values.entrySet()) {
			set(entry.getKey(), entry.getValue());
		}
	}
}
