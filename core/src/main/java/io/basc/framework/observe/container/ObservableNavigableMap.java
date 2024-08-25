package io.basc.framework.observe.container;

import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.function.Supplier;

import io.basc.framework.observe.PublishService;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.observe.ChangeEvent;
import io.basc.framework.util.register.container.AtomicEntryRegistration;
import lombok.NonNull;

public class ObservableNavigableMap<K, V, C extends NavigableMap<K, AtomicEntryRegistration<K, V>>, R extends NavigableMap<K, V>, T extends NavigableSet<K>>
		extends ObservableSortedMap<K, V, C, R> implements NavigableMap<K, V> {
	@NonNull
	private final Function<? super NavigableSet<K>, ? extends T> resultSetCloner;

	public ObservableNavigableMap(@NonNull Supplier<? extends C> containerSupplier,
			@NonNull PublishService<ChangeEvent<KeyValue<K, V>>> publishService,
			@NonNull Function<? super SortedMap<K, AtomicEntryRegistration<K, V>>, ? extends R> resultCloner,
			@NonNull Function<? super NavigableSet<K>, ? extends T> resultSetCloner) {
		super(containerSupplier, publishService, resultCloner);
		this.resultSetCloner = resultSetCloner;
	}

	@Override
	public final Entry<K, V> ceilingEntry(K key) {
		return getEntry((map) -> map.ceilingEntry(key));
	}

	@Override
	public final K ceilingKey(K key) {
		return getKey((map) -> map.ceilingKey(key));
	}

	@Override
	public final T descendingKeySet() {
		return getSet((map) -> map.descendingKeySet());
	}

	@Override
	public final R descendingMap() {
		return getMap((map) -> map.descendingMap());
	}

	@Override
	public final Entry<K, V> firstEntry() {
		return getEntry((map) -> map.firstEntry());
	}

	@Override
	public final Entry<K, V> floorEntry(K key) {
		return getEntry((map) -> map.floorEntry(key));
	}

	@Override
	public final K floorKey(K key) {
		return getKey((map) -> map.floorKey(key));
	}

	/**
	 * 获取entry
	 * 
	 * @param getter 回调参数不会为空
	 * @return
	 */
	public final Entry<K, V> getEntry(Function<? super C, ? extends Entry<K, AtomicEntryRegistration<K, V>>> getter) {
		Entry<K, AtomicEntryRegistration<K, V>> entry = read((map) -> {
			if (map == null) {
				return null;
			}

			return getter.apply(map);
		});

		if (entry == null) {
			return null;
		}

		return entry.getValue();
	}

	public final T getSet(Function<? super C, ? extends NavigableSet<K>> getter) {
		return read((map) -> {
			if (map == null) {
				return resultSetCloner.apply(null);
			}

			NavigableSet<K> set = getter.apply(map);
			return resultSetCloner.apply(set);
		});
	}

	@Override
	public final R headMap(K toKey, boolean inclusive) {
		return getMap((map) -> map.headMap(toKey, inclusive));
	}

	@Override
	public final Entry<K, V> higherEntry(K key) {
		return getEntry((map) -> map.higherEntry(key));
	}

	@Override
	public final K higherKey(K key) {
		return getKey((map) -> map.higherKey(key));
	}

	@Override
	public final Entry<K, V> lastEntry() {
		return getEntry((map) -> map.lastEntry());
	}

	@Override
	public final Entry<K, V> lowerEntry(K key) {
		return getEntry((map) -> map.lowerEntry(key));
	}

	@Override
	public final K lowerKey(K key) {
		return getKey((map) -> map.lowerKey(key));
	}

	@Override
	public final T navigableKeySet() {
		return getSet((map) -> map.navigableKeySet());
	}

	public final Entry<K, V> poll(Function<? super C, ? extends Entry<K, AtomicEntryRegistration<K, V>>> poller) {
		Entry<K, AtomicEntryRegistration<K, V>> entry = update((map) -> poller.apply(map));
		if (entry == null) {
			return null;
		}

		AtomicEntryRegistration<K, V> registration = entry.getValue();
		if (registration == null) {
			return null;
		}

		registration.deregister();
		return registration;
	}

	@Override
	public final Entry<K, V> pollFirstEntry() {
		return poll((map) -> map.pollFirstEntry());
	}

	@Override
	public final Entry<K, V> pollLastEntry() {
		return poll((map) -> map.pollLastEntry());
	}

	@Override
	public final R subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
		return getMap((map) -> map.subMap(fromKey, fromInclusive, toKey, toInclusive));
	}

	@Override
	public final R tailMap(K fromKey, boolean inclusive) {
		return getMap((map) -> map.tailMap(fromKey, inclusive));
	}
}
