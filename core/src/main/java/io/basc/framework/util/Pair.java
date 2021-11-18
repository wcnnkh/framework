package io.basc.framework.util;

import java.io.Serializable;

public class Pair<K, V> implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private K key;
	private V value;

	public Pair() {
	}

	public Pair(Pair<K, V> pair) {
		this(pair.key, pair.value);
	}

	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public K getKey() {
		return key;
	}

	public V getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		int code = 0;
		if (key != null) {
			code += key.hashCode();
		}

		if (value != null) {
			code += value.hashCode();
		}
		return code;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj instanceof Pair) {
			return ObjectUtils.equals(((Pair) obj).getKey(), getKey())
					&& ObjectUtils.equals(((Pair) obj).getValue(), getValue());
		}
		return false;
	}

	@Override
	public String toString() {
		return "key=" + key + ", value=" + value;
	}

	@Override
	protected Pair<K, V> clone() throws CloneNotSupportedException {
		return new Pair<K, V>(key, value);
	}
}
