package run.soeasy.framework.core.collection;

import java.util.List;
import java.util.Map;

@FunctionalInterface
public interface MultiValueMapWrapper<K, V, W extends MultiValueMap<K, V>>
		extends MultiValueMap<K, V>, MapWrapper<K, List<V>, W> {
	@Override
	default V getFirst(Object key) {
		return getSource().getFirst(key);
	}

	@Override
	default void adds(K key, List<V> values) {
		getSource().adds(key, values);
	}

	@Override
	default void set(K key, V value) {
		getSource().set(key, value);
	}

	@Override
	default void add(K key, V value) {
		getSource().add(key, value);
	}

	@Override
	default void setAll(Map<? extends K, ? extends V> map) {
		getSource().setAll(map);
	}

	@Override
	default void addAll(Map<? extends K, ? extends List<V>> map) {
		getSource().addAll(map);
	}

	@Override
	default Map<K, V> toSingleValueMap() {
		return getSource().toSingleValueMap();
	}

}