package io.basc.framework.observe.register;

import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.SortedMap;
import java.util.function.Function;
import java.util.function.Supplier;

import io.basc.framework.register.PayloadRegistration;
import lombok.NonNull;

public class ObservableNavigableMap<K, V, M extends NavigableMap<K, PayloadRegistration<Entry<K, V>>>, R extends NavigableMap<K, V>, T extends NavigableSet<K>>
		extends ObservableSortedMap<K, V, M, R> implements NavigableMap<K, V> {
	private final Function<? super NavigableSet<K>, ? extends T> setCloner;

	public ObservableNavigableMap(@NonNull Supplier<? extends M> containerSupplier,
			@NonNull Function<? super SortedMap<K, PayloadRegistration<Entry<K, V>>>, ? extends R> mapCloner,
			@NonNull Function<? super NavigableSet<K>, ? extends T> setCloner) {
		super(containerSupplier, mapCloner);
		this.setCloner = setCloner;
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
	public final Entry<K, V> getEntry(
			Function<? super M, ? extends Entry<K, PayloadRegistration<Entry<K, V>>>> getter) {
		Entry<K, PayloadRegistration<Entry<K, V>>> entry = read((map) -> {
			if (map == null) {
				return null;
			}

			return getter.apply(map);
		});

		if (entry == null) {
			return null;
		}

		PayloadRegistration<Entry<K, V>> elementRegistration = entry.getValue();
		return elementRegistration == null ? null : elementRegistration.getPayload();
	}

	public final T getSet(Function<? super M, ? extends NavigableSet<K>> getter) {
		return read((map) -> {
			if (map == null) {
				return setCloner.apply(null);
			}

			NavigableSet<K> set = getter.apply(map);
			return setCloner.apply(set);
		});
	}

	public final Function<? super NavigableSet<K>, ? extends T> getSetCloner() {
		return setCloner;
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

	public final Entry<K, V> poll(Function<? super M, ? extends Entry<K, PayloadRegistration<Entry<K, V>>>> poller) {
		Entry<K, PayloadRegistration<Entry<K, V>>> entry = update((map) -> poller.apply(map));
		if (entry == null) {
			return null;
		}

		PayloadRegistration<Entry<K, V>> registration = entry.getValue();
		if (registration == null) {
			return null;
		}

		registration.unregister();
		return registration.getPayload();
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
