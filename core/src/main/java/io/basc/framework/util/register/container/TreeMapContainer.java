package io.basc.framework.util.register.container;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.actor.ChangeType;

public class TreeMapContainer<K, V> extends MapContainer<K, V, TreeMap<K, EntryRegistration<K, V>>> {
	private Comparator<? super K> comparator;

	public TreeMapContainer() {
		super(TreeMap::new);
	}

	@Override
	protected TreeMap<K, EntryRegistration<K, V>> newContainer() {
		if (comparator != null) {
			return new TreeMap<>(comparator);
		}
		return super.newContainer();
	}

	public Comparator<? super K> getComparator() {
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
				this.comparator = comparator;
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
				getPublisher().publish(Elements.of(events));
				return true;
			});
		} finally {
			lock.unlock();
		}
	}
}
