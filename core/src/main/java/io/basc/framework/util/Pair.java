package io.basc.framework.util;

import io.basc.framework.env.BascObject;

import java.io.Serializable;

public class Pair<K, V> extends BascObject implements Serializable {
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
}
