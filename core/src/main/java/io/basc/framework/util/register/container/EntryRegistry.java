package io.basc.framework.util.register.container;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.basc.framework.util.concurrent.AtomicEntry;
import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.BatchRegistration;
import io.basc.framework.util.register.PayloadRegistration;
import lombok.NonNull;

public class EntryRegistry<K, V, M extends Map<K, EntryRegistration<K, V>>>
		extends AbstractRegistry<Entry<K, V>, M, EntryRegistration<K, V>> implements Map<K, V> {

	public EntryRegistry(@NonNull Supplier<? extends M> containerSupplier) {
		super(containerSupplier);
	}

	protected EntryRegistration<K, V> createRegistration(Entry<K, V> entry) {
		return new EntryRegistration<>(entry);
	}

	@Override
	protected BatchRegistration<EntryRegistration<K, V>> createBatchRegistration(
			Iterable<? extends Entry<K, V>> items) {
		return new ContainerBatchRegistration<>(Elements.of(items).map(this::createRegistration), (a, b) -> a.and(b));
	}

	@Override
	protected BatchRegistration<EntryRegistration<K, V>> batch(
			BatchRegistration<EntryRegistration<K, V>> batchRegistration) {
		return batchRegistration.batch((es) -> () -> {
			execute((map) -> {
				es.forEach((e) -> map.remove(e.getKey(), e));
				return true;
			});
		});
	}

	@Override
	protected boolean register(M container, EntryRegistration<K, V> registration) {
		if (container.containsKey(registration.getKey())) {
			registration.getLimiter().limited();
			return false;
		}

		container.put(registration.getKey(), registration);
		return true;
	}

	@Override
	public Elements<EntryRegistration<K, V>> getRegistrations() {
		return read((map) -> {
			// 先拷贝出来，防止出现变化
			if (map == null) {
				return Elements.empty();
			}

			return Elements.of(map.entrySet().stream().map((e) -> e.getValue()).filter((e) -> !e.isInvalid())
					.collect(Collectors.toList()));
		});
	}

	@Override
	public final int size() {
		return readInt((map) -> map == null ? 0 : map.size());
	}

	@Override
	public final boolean isEmpty() {
		return test((map) -> map == null ? true : map.isEmpty());
	}

	@Override
	public final boolean containsKey(Object key) {
		return test((map) -> map == null ? false : map.containsKey(key));
	}

	@Override
	public final boolean containsValue(Object value) {
		return read((map) -> map == null ? false : map.containsValue(value));
	}

	@Override
	public final V get(Object key) {
		return getValue((map) -> map.get(key));
	}

	protected Entry<K, V> createEntry(K key, V value) {
		AtomicEntry<K, V> entry = new AtomicEntry<>(key);
		entry.setValue(value);
		return entry;
	}

	@Override
	public final V put(K key, V value) {
		return write((map) -> put(map, key, value));
	}

	protected V put(M container, K key, V value) {
		EntryRegistration<K, V> current = container.get(key);
		if (current == null) {
			Entry<K, V> entry = createEntry(key, value);
			BatchRegistration<EntryRegistration<K, V>> batchRegistration = createBatchRegistration(
					Elements.singleton(entry));
			current = batchRegistration.getRegistrations().first();
			container.put(key, current);
			return null;
		} else {
			return current.setValue(value);
		}
	}

	@Override
	public final V remove(Object key) {
		EntryRegistration<K, V> entryRegistration = update((map) -> {
			if (map == null) {
				return null;
			}

			return map.remove(key);
		});

		if (entryRegistration == null) {
			return null;
		}

		entryRegistration.getLimiter().limited();
		return entryRegistration.getValue();
	}

	/**
	 * 获取值
	 * 
	 * @param getter 回调参数不会为空
	 * @return
	 */
	public final V getValue(Function<? super M, ? extends PayloadRegistration<Entry<K, V>>> getter) {
		PayloadRegistration<Entry<K, V>> elementRegistration = read((map) -> {
			if (map == null) {
				return null;
			}
			return getter.apply(map);
		});

		if (elementRegistration == null) {
			return null;
		}

		Entry<K, V> entry = elementRegistration.getPayload();
		return entry == null ? null : entry.getValue();
	}

	@Override
	public final void putAll(Map<? extends K, ? extends V> m) {
		write((map) -> {
			for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
				put(entry.getKey(), entry.getValue());
			}
			return null;
		});
	}

	@Override
	public void clear() {
		deregister();
	}

	@Override
	public final Set<K> keySet() {
		return read((map) -> {
			if (map == null || map.isEmpty()) {
				return Collections.emptySet();
			}

			// 先拷贝出来，防止出现变化
			return map.keySet().stream().collect(Collectors.toCollection(LinkedHashSet::new));
		});
	}

	@Override
	public final Collection<V> values() {
		return read((map) -> {
			if (map == null || map.isEmpty()) {
				return Collections.emptyList();
			}

			// 先拷贝出来，防止出现变化
			return map.values().stream().map((e) -> e.getPayload().getValue())
					.collect(Collectors.toCollection(LinkedHashSet::new));
		});
	}

	@Override
	public final Set<Entry<K, V>> entrySet() {
		return read((map) -> {
			if (map == null || map.isEmpty()) {
				return Collections.emptySet();
			}

			return map.entrySet().stream().map((e) -> e.getValue().getPayload())
					.collect(Collectors.toCollection(LinkedHashSet::new));
		});
	}
}