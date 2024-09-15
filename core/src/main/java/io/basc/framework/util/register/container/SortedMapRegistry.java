package io.basc.framework.util.register.container;

import java.util.Map;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.function.Supplier;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.event.ChangeEvent;
import lombok.NonNull;

public abstract class SortedMapRegistry<K, V, M extends SortedMap<K, EntryRegistration<K, V>>>
		extends MapRegistry<K, V, M> implements SortedMap<K, V> {

	public SortedMapRegistry(Supplier<? extends M> containerSupplier,
			@NonNull Publisher<? super Elements<ChangeEvent<KeyValue<K, V>>>> changeEventsPublisher) {
		super(containerSupplier, changeEventsPublisher);
	}

	public abstract SortedMap<K, V> getMap(Function<? super M, ? extends Map<K, EntryRegistration<K, V>>> getter);

	@Override
	public final SortedMap<K, V> subMap(K fromKey, K toKey) {
		return getMap((map) -> map.subMap(fromKey, toKey));
	}

	@Override
	public final SortedMap<K, V> headMap(K toKey) {
		return getMap((map) -> map.headMap(toKey));
	}

	@Override
	public final SortedMap<K, V> tailMap(K fromKey) {
		return getMap((map) -> map.tailMap(fromKey));
	}

	@Override
	public final K firstKey() {
		return read((map) -> map == null ? null : map.firstKey());
	}

	@Override
	public final K lastKey() {
		return read((map) -> map == null ? null : map.lastKey());
	}
}
