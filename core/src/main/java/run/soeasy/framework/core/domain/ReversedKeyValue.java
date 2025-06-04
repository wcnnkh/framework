package run.soeasy.framework.core.domain;

import java.io.Serializable;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ReversedKeyValue<V, K, W extends KeyValue<K, V>> implements KeyValue<V, K>, Serializable {
	private static final long serialVersionUID = 1L;
	@NonNull
	private final W reversed;

	@Override
	public V getKey() {
		return reversed.getValue();
	}

	@Override
	public K getValue() {
		return reversed.getKey();
	}

	@Override
	public KeyValue<K, V> reversed() {
		return reversed;
	}
}
