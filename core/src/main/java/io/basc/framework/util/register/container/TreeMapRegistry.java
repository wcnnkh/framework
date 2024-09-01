package io.basc.framework.util.register.container;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Function;

import io.basc.framework.util.KeyValue;
import io.basc.framework.util.event.EventPublishService;
import io.basc.framework.util.observe.ChangeEvent;
import lombok.NonNull;

public class TreeMapRegistry<K, V> extends NavigableMapRegistry<K, V, TreeMap<K, EntryRegistration<K, V>>> {
	private final Comparator<? super K> comparator;

	public TreeMapRegistry(Comparator<? super K> comparator,
			@NonNull EventPublishService<ChangeEvent<KeyValue<K, V>>> eventPublishService) {
		super(() -> new TreeMap<>(comparator), eventPublishService);
		this.comparator = comparator;
	}

	@Override
	public Comparator<? super K> comparator() {
		return comparator;
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
				if (registration == null || registration.isInvalid()) {
					continue;
				}

				treeMap.put(registration.getKey(), registration.getValue());
			}
			return treeMap;
		});
	}
}
