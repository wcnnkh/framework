package scw.core.lazy;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
public abstract class AbstractMapLazyFactory<K, V> implements
		MapLazyFactory<K, V> {

	public Map<K, V> createMap() {
		return new HashMap<K, V>();
	}

	/**
	 * 是否应该创建一个新值
	 * @param key
	 * @param value
	 * @return
	 */
	protected boolean isCreateNewValue(K key, V value) {
		return value == null;
	}

	protected abstract Map<K, V> getMap(boolean init);

	public int size() {
		Map<K, V> map = getMap(false);
		return map == null ? 0 : map.size();
	}

	public boolean isEmpty() {
		Map<K, V> map = getMap(false);
		return map == null ? true : map.isEmpty();
	}

	public boolean containsKey(Object key) {
		Map<K, V> map = getMap(false);
		return map == null ? false : map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		Map<K, V> map = getMap(false);
		return map == null ? false : map.containsValue(value);
	}

	public Set<K> keySet() {
		Map<K, V> map = getMap(false);
		return map == null ? Collections.EMPTY_SET : map.keySet();
	}

	public Collection<V> values() {
		Map<K, V> map = getMap(false);
		return map == null ? Collections.EMPTY_LIST : map.values();
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		Map<K, V> map = getMap(false);
		return map == null ? Collections.EMPTY_SET : map.entrySet();
	}
}
