package run.soeasy.framework.util;

import java.io.Serializable;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface KeyValue<K, V> {

	@RequiredArgsConstructor
	@AllArgsConstructor
	@Getter
	public static class StandardKeyValue<K, V> implements KeyValue<K, V>, Entry<K, V>, Serializable {
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

	K getKey();

	V getValue();

	public static <K, V> KeyValue<K, V> of(K key, V value) {
		return new StandardKeyValue<>(key, value);
	}
}
