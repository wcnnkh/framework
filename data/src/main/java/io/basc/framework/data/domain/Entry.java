package io.basc.framework.data.domain;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Entry<K, V> implements Map.Entry<K, V> {
	private final K key;
	private V value;

	@Override
	public V setValue(V value) {
		V previous = this.value;
		this.value = value;
		return previous;
	}
}
