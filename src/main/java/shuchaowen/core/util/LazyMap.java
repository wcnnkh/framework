package shuchaowen.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import shuchaowen.core.exception.KeyNonexistentException;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public class LazyMap<K, V> {
	private Map<K, Value<V>> map;

	public LazyMap() {
		this.map = new HashMap<K, Value<V>>();
	}

	/**
	 * 获取值
	 * 
	 * @param key
	 * @param callable
	 *            如果key不存在就会调用callable并保存结果
	 * @return
	 */
	public V get(final K key, final Callable<V> callable) {
		if (containsKey(key)) {
			return map.get(key).getValue();
		} else {
			synchronized (map) {
				if (containsKey(key)) {
					return map.get(key).getValue();
				} else {
					Value<V> value = new Value<V>(callable);
					map.put(key, value);
					return value.getValue();
				}
			}
		}
	}

	/**
	 * 查找此值
	 * 
	 * @param key
	 * @param reloadValue
	 * @return
	 * @throws KeyNonexistentException
	 */
	public V get(final K key, boolean reloadValue) throws KeyNonexistentException {
		if (reloadValue) {
			if (containsKey(key)) {
				synchronized (map) {
					return map.get(key).reloadValue();
				}
			} else {
				throw new KeyNonexistentException(key + "");
			}
		} else if (map.containsKey(key)) {
			return map.get(key).getValue();
		}
		throw new KeyNonexistentException(key + " not found");
	}

	public boolean containsKey(K key) {
		return map.containsKey(key);
	}
}

class Value<V> {
	private V value;
	private Callable<V> callable;

	public Value() {
	}

	public Value(Callable<V> callable) {
		try {
			this.value = callable.call();
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
		this.callable = callable;
	}

	public V reloadValue() {
		try {
			this.value = callable.call();
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
		return value;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public Callable<V> getCallable() {
		return callable;
	}

	public void setCallable(Callable<V> callable) {
		this.callable = callable;
	}
}
