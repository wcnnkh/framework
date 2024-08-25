package io.basc.framework.observe.register;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

import io.basc.framework.observe.PublishService;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.observe.ChangeEvent;
import io.basc.framework.util.register.PayloadRegistration;
import lombok.NonNull;

public class ObservableTreeMap<K, V> extends
		ObservableNavigableMap<K, V, TreeMap<K, PayloadRegistration<Entry<K, V>>>, NavigableMap<K, V>, NavigableSet<K>> {
	private final Comparator<? super K> comparator;

	public ObservableTreeMap(Comparator<? super K> comparator,
			@NonNull PublishService<ChangeEvent<KeyValue<K, V>>> publishService) {
		super(() -> new TreeMap<>(comparator), publishService,
				(map) -> CollectionUtils.isEmpty(map) ? Collections.emptyNavigableMap() : new TreeMap<>(comparator),
				(set) -> CollectionUtils.isEmpty(set) ? Collections.emptyNavigableSet() : new TreeSet<>(comparator));
		this.comparator = comparator;
	}

	@Override
	public final Comparator<? super K> comparator() {
		return comparator;
	}
}
