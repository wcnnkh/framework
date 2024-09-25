package io.basc.framework.util.register.container;

import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.function.Function;
import java.util.function.Supplier;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.Publisher;
import io.basc.framework.util.actor.ChangeEvent;
import io.basc.framework.util.actor.ChangeType;
import lombok.NonNull;

public abstract class NavigableMapRegistry<K, V, M extends NavigableMap<K, EntryRegistration<K, V>>>
		extends SortedMapRegistry<K, V, M> implements NavigableMap<K, V> {

	public NavigableMapRegistry(Supplier<? extends M> containerSupplier,
			@NonNull Publisher<? super Elements<ChangeEvent<KeyValue<K, V>>>> changeEventsPublisher) {
		super(containerSupplier, changeEventsPublisher);
	}

	@Override
	public Entry<K, V> lowerEntry(K key) {
		return getEntry((map) -> map.lowerEntry(key));
	}

	@Override
	public K lowerKey(K key) {
		return read((map) -> map == null ? null : map.lowerKey(key));
	}

	@Override
	public Entry<K, V> floorEntry(K key) {
		return getEntry((map) -> map == null ? null : map.floorEntry(key));
	}

	@Override
	public K floorKey(K key) {
		return read((map) -> map == null ? null : map.floorKey(key));
	}

	@Override
	public Entry<K, V> ceilingEntry(K key) {
		return getEntry((map) -> map.ceilingEntry(key));
	}

	@Override
	public K ceilingKey(K key) {
		return read((map) -> map == null ? null : map.ceilingKey(key));
	}

	@Override
	public Entry<K, V> higherEntry(K key) {
		return getEntry((map) -> map.higherEntry(key));
	}

	@Override
	public K higherKey(K key) {
		return read((map) -> map == null ? null : map.higherKey(key));
	}

	@Override
	public Entry<K, V> firstEntry() {
		return getEntry((map) -> map.firstEntry());
	}

	@Override
	public Entry<K, V> lastEntry() {
		return getEntry((map) -> map.lastEntry());
	}

	public final Entry<K, V> poll(Function<? super M, ? extends Entry<K, EntryRegistration<K, V>>> poller) {
		return update((map) -> {
			if (map == null) {
				return null;
			}

			Entry<K, EntryRegistration<K, V>> entry = poller.apply(map);
			if (entry == null) {
				return null;
			}

			EntryRegistration<K, V> registration = entry.getValue();
			if (registration == null) {
				return null;
			}

			registration.cancel();
			getChangeEventsPublisher()
					.publish(Elements.singleton(new ChangeEvent<KeyValue<K, V>>(registration, ChangeType.DELETE)));
			return registration;
		});
	}

	@Override
	public Entry<K, V> pollFirstEntry() {
		return poll((map) -> map.pollFirstEntry());
	}

	@Override
	public Entry<K, V> pollLastEntry() {
		return poll((map) -> map.pollLastEntry());
	}

	@Override
	public NavigableMap<K, V> descendingMap() {
		// TODO 返回的视图应该支持操作吗？
		return getMap((map) -> map.descendingMap());
	}

	@Override
	public NavigableSet<K> navigableKeySet() {
		// TODO 返回的视图应该支持操作吗？
		return read((map) -> map == null ? null : map.navigableKeySet());
	}

	@Override
	public NavigableSet<K> descendingKeySet() {
		// TODO 返回的视图应该支持操作吗？
		return read((map) -> map == null ? null : map.descendingKeySet());
	}

	@Override
	public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
		return getMap((map) -> map.subMap(fromKey, fromInclusive, toKey, toInclusive));
	}

	@Override
	public NavigableMap<K, V> headMap(K toKey, boolean inclusive) {
		return getMap((map) -> map.headMap(toKey, inclusive));
	}

	@Override
	public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive) {
		return getMap((map) -> map.tailMap(fromKey, inclusive));
	}

	@Override
	public abstract NavigableMap<K, V> getMap(Function<? super M, ? extends Map<K, EntryRegistration<K, V>>> getter);
}
