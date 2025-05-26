package run.soeasy.framework.core.domain;

import java.io.Serializable;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class CustomizeKeyValue<K, V> implements KeyValue<K, V>, Entry<K, V>, Serializable {
	private static final long serialVersionUID = 1L;
	private final K key;
	private V value;

	@Override
	public V setValue(V value) {
		V old = this.value;
		this.value = value;
		return old;
	}
}