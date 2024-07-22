package io.basc.framework.observe.register;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Supplier;

import io.basc.framework.register.PayloadRegistration;
import lombok.NonNull;

public class ObservableMap<K, V> extends EntryRegistry<K, V, Map<K, PayloadRegistration<Entry<K, V>>>> {
	public ObservableMap() {
		this(() -> new LinkedHashMap<>());
	}

	public ObservableMap(Comparator<? super K> comparator) {
		this(() -> new TreeMap<>(comparator));
	}

	public ObservableMap(@NonNull Supplier<? extends Map<K, PayloadRegistration<Entry<K, V>>>> containerSupplier) {
		super(containerSupplier);
	}

}
