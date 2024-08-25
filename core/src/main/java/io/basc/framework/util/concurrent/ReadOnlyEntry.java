package io.basc.framework.util.concurrent;

import java.io.Serializable;
import java.util.Map.Entry;

import io.basc.framework.util.KeyValue;
import lombok.Data;

@Data
public class ReadOnlyEntry<K, V> implements Entry<K, V>, KeyValue<K, V>, Serializable {
	private static final long serialVersionUID = 1L;
	private final K key;
	private final V value;

	@Override
	public V setValue(V value) {
		throw new UnsupportedOperationException("readOnly");
	}
}
