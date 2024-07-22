package io.basc.framework.observe.register;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

import io.basc.framework.register.PayloadRegistration;
import io.basc.framework.util.CollectionUtils;

public class ObservableTreeMap<K, V> extends
		ObservableNavigableMap<K, V, TreeMap<K, PayloadRegistration<Entry<K, V>>>, NavigableMap<K, V>, NavigableSet<K>> {
	private final Comparator<? super K> comparator;

	public ObservableTreeMap(Comparator<? super K> comparator) {
		super(() -> new TreeMap<>(comparator),
				(map) -> CollectionUtils.isEmpty(map) ? Collections.emptyNavigableMap() : new TreeMap<>(comparator),
				(set) -> CollectionUtils.isEmpty(set) ? Collections.emptyNavigableSet() : new TreeSet<>(comparator));
		this.comparator = comparator;
	}

	@Override
	public final Comparator<? super K> comparator() {
		return comparator;
	}
}
