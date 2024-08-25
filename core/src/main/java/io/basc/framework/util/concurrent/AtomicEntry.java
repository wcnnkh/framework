package io.basc.framework.util.concurrent;

import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import io.basc.framework.util.KeyValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AtomicEntry<K, V> extends AtomicReference<V> implements KeyValue<K, V>, Entry<K, V> {
	private static final long serialVersionUID = 1L;
	private final K key;
	
	@Override
	public V setValue(V value) {
		return getAndSet(value);
	}

	@Override
	public V getValue() {
		return get();
	}
}
