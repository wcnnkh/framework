package io.basc.framework.observe.container;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.function.Supplier;

import io.basc.framework.observe.PublishService;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.observe.ChangeEvent;
import io.basc.framework.util.register.container.AtomicEntryRegistration;
import lombok.NonNull;

public class ObservableSortedMap<K, V, C extends SortedMap<K, AtomicEntryRegistration<K, V>>, R extends SortedMap<K, V>>
		extends ObservableMap<K, V, C> implements SortedMap<K, V> {
	private final Function<? super SortedMap<K, AtomicEntryRegistration<K, V>>, ? extends R> resultCloner;

	public ObservableSortedMap(@NonNull Supplier<? extends C> containerSupplier,
			@NonNull PublishService<ChangeEvent<KeyValue<K, V>>> publishService,
			@NonNull Function<? super SortedMap<K, AtomicEntryRegistration<K, V>>, ? extends R> resultCloner) {
		super(containerSupplier, publishService);
		this.resultCloner = resultCloner;
	}

	@Override
	public Comparator<? super K> comparator() {
		return write((map) -> map.comparator());
	}

	public final R getMap(Function<? super C, ? extends SortedMap<K, AtomicEntryRegistration<K, V>>> getter) {
		return read((map) -> {
			if (map == null) {
				return resultCloner.apply(map);
			}

			SortedMap<K, AtomicEntryRegistration<K, V>> resutMap = getter.apply(map);
			return resultCloner.apply(resutMap);
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
	public final K getKey(Function<? super C, ? extends K> getter) {
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
