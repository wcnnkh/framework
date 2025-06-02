package run.soeasy.framework.core.collection;

import java.util.Collections;
import java.util.Map;

import run.soeasy.framework.core.domain.ObjectOperator;

/**
 * 使用数组实现的都会有一个初始容量(initialCapacity)和扩容因子(loadFactor)
 * 
 * @author soeasy.run
 *
 * @param <K>
 * @param <V>
 * @param <T>
 */
@FunctionalInterface
public interface MapFactory<K, V, T extends Map<K, V>> extends ObjectOperator<Map<K, V>> {
	public static final int DEFAULT_INITIAL_CAPACITY = 16;
	public static final float DEFAULT_LOAD_FACTOR = 0.75f;

	@Override
	default T create() {
		return createMap(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
	}

	@Override
	default Map<K, V> display(Map<K, V> source) {
		return Collections.unmodifiableMap(source);
	}

	@Override
	default T clone(Map<K, V> source) {
		int newCapacity = Math.max(source.size(), Math.round((source.size() + 1) * (DEFAULT_LOAD_FACTOR * 2 - 1)));
		T target = createMap(newCapacity, DEFAULT_LOAD_FACTOR);
		target.putAll(source);
		return target;
	}

	/**
	 * 使用数组实现的都会有一个初始容量(initialCapacity)和扩容因子(loadFactor)
	 * 
	 * @param initialCapacity
	 * @param loadFactor
	 * @return
	 */
	T createMap(int initialCapacity, float loadFactor);
}
