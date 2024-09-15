package io.basc.framework.observe.register;

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.function.Supplier;

import io.basc.framework.util.register.PayloadRegistration;
import lombok.NonNull;

public class ObservableSortedMap<K, V, M extends SortedMap<K, PayloadRegistration<Entry<K, V>>>, R extends SortedMap<K, V>>
		extends EntryRegistry<K, V, M> implements SortedMap<K, V> {
	private final Function<? super SortedMap<K, PayloadRegistration<Entry<K, V>>>, ? extends R> mapCloner;

	public ObservableSortedMap(@NonNull Supplier<? extends M> containerSupplier,
			@NonNull Function<? super SortedMap<K, PayloadRegistration<Entry<K, V>>>, ? extends R> mapCloner) {
		super(containerSupplier);
		this.mapCloner = mapCloner;
	}

	public final Function<? super SortedMap<K, PayloadRegistration<Entry<K, V>>>, ? extends R> getMapCloner() {
		return mapCloner;
	}

	@Override
	public Comparator<? super K> comparator() {
		return write((map) -> map.comparator());
	}

	public final R getMap(Function<? super M, ? extends SortedMap<K, PayloadRegistration<Entry<K, V>>>> getter) {
		return read((map) -> {
			if (map == null) {
				return mapCloner.apply(map);
			}

			SortedMap<K, PayloadRegistration<Entry<K, V>>> resultMap = getter.apply(map);
			return mapCloner.apply(resultMap);
		});
	}

	@Override
	public final R subMap(K fromKey, K toKey) {
		return getMap((map) -> map.subMap(fromKey, toKey));
	}

	@Override
	public final R headMap(K toKey) {
		return getMap((map) -> map.headMap(toKey));
	}

	@Override
	public final R tailMap(K fromKey) {
		return getMap((map) -> map.tailMap(fromKey));
	}

	/**
	 * 获取key
	 * 
	 * @param getter 回调参数不会为空
	 * @return
	 */
	public final K getKey(Function<? super M, ? extends K> getter) {
		return read((map) -> map == null ? null : getter.apply(map));
	}

	@Override
	public final K firstKey() {
		return getKey((map) -> map.firstKey());
	}

	@Override
	public final K lastKey() {
		return getKey((map) -> map.lastKey());
	}

}
