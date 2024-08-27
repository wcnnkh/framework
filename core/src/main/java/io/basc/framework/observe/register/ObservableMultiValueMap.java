package io.basc.framework.observe.register;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.basc.framework.observe.container.AbstractServiceRegistry;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.collect.MultiValueMap;
import io.basc.framework.util.register.BatchRegistration;
import io.basc.framework.util.register.PayloadRegistration;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.RegistrationException;
import lombok.NonNull;

public class ObservableMultiValueMap<K, V, L extends ObservableMultiValue<K, V>, M extends Map<K, L>>
		extends AbstractServiceRegistry<Entry<K, V>, M> implements MultiValueMap<K, V> {
	@NonNull
	private final Function<? super K, ? extends L> multiValueCreator;

	public ObservableMultiValueMap(@NonNull Supplier<? extends M> containerSupplier,
			@NonNull Function<? super K, ? extends L> multiValueCreator) {
		super(containerSupplier);
		this.multiValueCreator = multiValueCreator;
	}

	@Override
	public int size() {
		return readInt((e) -> e == null ? 0 : e.size());
	}

	@Override
	public boolean isEmpty() {
		return test((e) -> e == null ? true : e.isEmpty());
	}

	@Override
	public boolean containsKey(Object key) {
		return test((e) -> e == null ? false : e.containsKey(key));
	}

	@Override
	public boolean containsValue(Object value) {
		return test((e) -> e == null ? null : e.containsValue(value));
	}

	@Override
	public List<V> get(Object key) {
		return read((e) -> e == null ? null : e.get(key));
	}

	@Override
	public List<V> put(K key, List<V> value) {
		L oldList = write((map) -> {
			L list = multiValueCreator.apply(key);
			list.setEntryEventPublishService(this);
			list.addAll(value);
			return map.put(key, list);
		});

		if (oldList != null) {
			oldList.getRegistrations().unregister();
			oldList.setEntryEventPublishService(null);
		}
		return oldList;
	}

	@Override
	public List<V> remove(Object key) {
		L oldList = update((map) -> {
			if (map == null) {
				return null;
			}

			return map.remove(key);
		});

		if (oldList != null) {
			oldList.getRegistrations().unregister();
			oldList.setEntryEventPublishService(null);
		}
		return oldList;
	}

	@Override
	public void putAll(Map<? extends K, ? extends List<V>> m) {
		if (CollectionUtils.isEmpty(m)) {
			return;
		}

		m.forEach(this::put);
	}

	@Override
	public void clear() {
		getRegistrations().unregister();
	}

	@Override
	public Set<K> keySet() {
		return read((map) -> {
			if (map == null) {
				return Collections.emptySet();
			}

			return map.keySet().stream().collect(Collectors.toCollection(LinkedHashSet::new));
		});
	}

	@Override
	public Collection<List<V>> values() {
		return read((map) -> {
			if (map == null) {
				return Collections.emptyList();
			}

			return map.values().stream().collect(Collectors.toList());
		});
	}

	@Override
	public Set<Entry<K, List<V>>> entrySet() {
		return new SharedEntrySet();
	}

	private class SharedEntrySet extends AbstractSet<Entry<K, List<V>>> {

		@Override
		public int size() {
			return ObservableMultiValueMap.this.size();
		}

		@Override
		public Iterator<Entry<K, List<V>>> iterator() {
			return ObservableMultiValueMap.this.entrySet().iterator();
		}

		@Override
		public boolean add(Entry<K, List<V>> e) {
			return write((map) -> {
				if (map.containsKey(e.getKey())) {
					return false;
				}

				L list = multiValueCreator.apply(e.getKey());
				list.setEntryEventPublishService(ObservableMultiValueMap.this);
				list.addAll(e.getValue());
				map.put(e.getKey(), list);
				return true;
			});
		}

		@Override
		public boolean addAll(Collection<? extends Entry<K, List<V>>> c) {
			// TODO 先这样的，有空再做批量处理
			return c.stream().map(this::add).anyMatch((e) -> e);
		}
	}

	@Override
	public PayloadRegistration<Entry<K, V>> register(Entry<K, V> element) throws RegistrationException {
		return write((map) -> {
			L list = map.get(element.getKey());
			if (list == null) {
				list = multiValueCreator.apply(element.getKey());
				list.setEntryEventPublishService(this);
			}
			Registration registration = list.register(element.getValue());
			return new PayloadRegistration<Entry<K, V>>(registration, element);
		});
	}

	@Override
	public BatchRegistration<PayloadRegistration<Entry<K, V>>> getRegistrations() {
		List<PayloadRegistration<Entry<K, V>>> list = read((map) -> {
			if (map == null) {
				return Collections.emptyList();
			}
			return map.values().stream().flatMap((e) -> e.getEntryRegistrations().getServices().stream())
					.collect(Collectors.toList());
		});
		return new BatchRegistration<>(Elements.of(list));
	}

	@Override
	public Elements<Entry<K, V>> getServices() {
		return read((map) -> {
			if (map == null) {
				return Elements.empty();
			}

			List<Entry<K, V>> list = map.entrySet().stream().flatMap(
					(entry) -> entry.getValue().stream().map((value) -> new ObservableEntry<>(entry.getKey(), value)))
					.collect(Collectors.toList());
			return Elements.of(list);
		});
	}

	@Override
	public V getFirst(K key) {
		return read((map) -> {
			if (map == null) {
				return null;
			}

			L list = map.get(key);
			return list == null ? null : list.get(0);
		});
	}

	@Override
	public void add(K key, V value) {
		write((map) -> {
			L list = map.get(key);
			if (list == null) {
				list = multiValueCreator.apply(key);
				list.setEntryEventPublishService(this);
			}
			list.add(value);
			return null;
		});
	}

	@Override
	public void set(K key, V value) {
		write((map) -> {
			L list = map.get(key);
			if (list == null) {
				list = multiValueCreator.apply(key);
				list.setEntryEventPublishService(this);
			} else {
				list.clear();
			}
			list.add(value);
			return null;
		});
	}

	@Override
	public void setAll(Map<K, V> values) {
		// TODO 有空进行优化为批量处理
		values.forEach(this::set);
	}
}
