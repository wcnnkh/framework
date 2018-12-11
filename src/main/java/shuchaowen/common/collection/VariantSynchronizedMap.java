package shuchaowen.common.collection;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 和Map不同的地方是此类对put,putAll,remove,clear方法进行加锁
 * @author shuchaowen
 *
 * @param <K>
 * @param <V>
 */
public class VariantSynchronizedMap<K, V> implements Map<K, V>, Serializable{
	private static final long serialVersionUID = 1L;
	private final Map<K, V> map;
	
	public VariantSynchronizedMap(){
		this.map = new HashMap<K, V>();
	}
	
	public VariantSynchronizedMap(Map<K, V> map){
		this.map = map;
	}
	
	public int size() {
		return map.size();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public V get(Object key) {
		return map.get(key);
	}

	public V put(K key, V value) {
		synchronized (map) {
			return map.put(key, value);
		}
	}

	public V remove(Object key) {
		synchronized (map) {
			return map.remove(key);
		}
	}

	public void putAll(Map<? extends K, ? extends V> m) {
		synchronized (map) {
			map.putAll(m);
		}
	}

	public void clear() {
		synchronized (map) {
			map.clear();
		}
	}

	public Set<K> keySet() {
		return map.keySet();
	}

	public Collection<V> values() {
		return map.values();
	}

	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}

}
