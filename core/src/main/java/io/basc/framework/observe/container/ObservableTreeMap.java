package io.basc.framework.observe.container;

import java.util.Collections;
import java.util.Comparator;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

import io.basc.framework.observe.PublishService;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.observe.event.ChangeEvent;
import io.basc.framework.util.observe.register.container.AtomicEntryRegistration;
import lombok.NonNull;

public class ObservableTreeMap<K, V>
		extends ObservableNavigableMap<K, V, TreeMap<K, AtomicEntryRegistration<K, V>>, NavigableMap<K, V>, NavigableSet<K>> {
	private final Comparator<? super K> comparator;

	public ObservableTreeMap(Comparator<? super K> comparator,
			@NonNull PublishService<ChangeEvent<KeyValue<K, V>>> publishService) {
		super(() -> new TreeMap<>(comparator), publishService,
				(map) -> CollectionUtils.isEmpty(map) ? Collections.emptyNavigableMap() : new TreeMap<>(comparator),
				(set) -> CollectionUtils.isEmpty(set) ? Collections.emptyNavigableSet() : new TreeSet<>(comparator));
		this.comparator = comparator;
	}

	@Override
	public Comparator<? super K> comparator() {
		return comparator;
	}
}
