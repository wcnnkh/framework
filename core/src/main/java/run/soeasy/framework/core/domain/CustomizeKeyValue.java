package run.soeasy.framework.core.domain;

import java.io.Serializable;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomizeKeyValue<K, V> implements KeyValue<K, V>, Entry<K, V>, Serializable {
	private static final long serialVersionUID = 1L;
	private K key;
	private V value;

	@Override
	public V setValue(V value) {
		V old = this.value;
		this.value = value;
		return old;
	}
}