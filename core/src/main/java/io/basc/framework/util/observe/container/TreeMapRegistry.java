package io.basc.framework.util.observe.container;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.observe.Publisher;
import io.basc.framework.util.observe.event.ChangeEvent;
import io.basc.framework.util.observe.event.ChangeType;
import lombok.NonNull;

public class TreeMapRegistry<K, V> extends NavigableMapRegistry<K, V, TreeMap<K, EntryRegistration<K, V>>> {
	private Comparator<? super K> comparator;

	public TreeMapRegistry(@NonNull Publisher<? super Elements<ChangeEvent<KeyValue<K, V>>>> changeEventsPublisher) {
		super(TreeMap::new, changeEventsPublisher);
	}

	@Override
	protected TreeMap<K, EntryRegistration<K, V>> newContainer() {
		if (comparator != null) {
			return new TreeMap<>(comparator);
		}
		return super.newContainer();
	}

	@Override
	public Comparator<? super K> comparator() {
		return comparator;
	}

	public void setComparator(Comparator<? super K> comparator) {
		if (comparator == this.comparator) {
			return;
		}

		Lock lock = getReadWriteLock().writeLock();
		lock.lock();
		try {
			update((map) -> {
				if (map == null) {
					return false;
				}

				TreeMap<K, EntryRegistration<K, V>> container = newContainer();
				container.putAll(map);
				map.clear();
				map.putAll(container);
				List<ChangeEvent<KeyValue<K, V>>> events = container.values().stream()
						.map((e) -> new ChangeEvent<>((KeyValue<K, V>) e, ChangeType.UPDATE))
						.collect(Collectors.toList());
				getChangeEventsPublisher().publish(Elements.of(events));
				return true;
			});
		} finally {
			lock.unlock();
		}
	}

	@Override
	public NavigableMap<K, V> getMap(
			Function<? super TreeMap<K, EntryRegistration<K, V>>, ? extends Map<K, EntryRegistration<K, V>>> getter) {
		return read((map) -> {
			if (map == null) {
				return Collections.emptyNavigableMap();
			}

			Map<K, EntryRegistration<K, V>> targetMap = getter.apply(map);
			if (targetMap == null) {
				return Collections.emptyNavigableMap();
			}

			TreeMap<K, V> treeMap = new TreeMap<>(comparator);
			for (Entry<K, EntryRegistration<K, V>> entry : targetMap.entrySet()) {
				EntryRegistration<K, V> registration = entry.getValue();
				if (registration == null || registration.isCancelled()) {
					continue;
				}

				treeMap.put(registration.getKey(), registration.getValue());
			}
			return treeMap;
		});
	}
}
