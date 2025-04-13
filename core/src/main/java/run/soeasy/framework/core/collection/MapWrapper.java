package run.soeasy.framework.core.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import run.soeasy.framework.core.Wrapper;

@FunctionalInterface
public interface MapWrapper<K, V, W extends Map<K, V>> extends Map<K, V>, Wrapper<W> {
	@Override
	default void clear() {
		getSource().clear();
	}

	@Override
	default boolean containsKey(Object key) {
		return getSource().containsKey(key);
	}

	@Override
	default boolean containsValue(Object value) {
		return getSource().containsValue(value);
	}

	@Override
	default Set<Entry<K, V>> entrySet() {
		return getSource().entrySet();
	}

	@Override
	default V get(Object key) {
		return getSource().get(key);
	}

	@Override
	default boolean isEmpty() {
		return getSource().isEmpty();
	}

	@Override
	default Set<K> keySet() {
		return getSource().keySet();
	}

	@Override
	default V put(K key, V value) {
		return getSource().put(key, value);
	}

	@Override
	default void putAll(Map<? extends K, ? extends V> m) {
		getSource().putAll(m);
	}

	@Override
	default V remove(Object key) {
		return getSource().remove(key);
	}

	@Override
	default int size() {
		return getSource().size();
	}

	@Override
	default Collection<V> values() {
		return getSource().values();
	}

	@Override
	default V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return getSource().compute(key, remappingFunction);
	}

	@Override
	default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
		return getSource().computeIfAbsent(key, mappingFunction);
	}

	@Override
	default V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
		return getSource().computeIfPresent(key, remappingFunction);
	}

	@Override
	default void forEach(BiConsumer<? super K, ? super V> action) {
		getSource().forEach(action);
	}

	@Override
	default V getOrDefault(Object key, V defaultValue) {
		return getSource().getOrDefault(key, defaultValue);
	}

	@Override
	default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return getSource().merge(key, value, remappingFunction);
	}

	@Override
	default V putIfAbsent(K key, V value) {
		return getSource().putIfAbsent(key, value);
	}

	@Override
	default boolean remove(Object key, Object value) {
		return getSource().remove(key, value);
	}

	@Override
	default boolean replace(K key, V oldValue, V newValue) {
		return getSource().replace(key, oldValue, newValue);
	}

	@Override
	default V replace(K key, V value) {
		return getSource().replace(key, value);
	}

	@Override
	default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
		getSource().replaceAll(function);
	}
}
