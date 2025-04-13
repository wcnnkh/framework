package run.soeasy.framework.core.concurrent;

import java.io.Serializable;
import java.util.Map.Entry;

import lombok.Data;
import run.soeasy.framework.core.KeyValue;

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
