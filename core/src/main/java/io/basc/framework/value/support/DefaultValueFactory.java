package io.basc.framework.value.support;

import io.basc.framework.event.support.StandardObservableMap;
import io.basc.framework.value.ConfigurableValueFactory;
import io.basc.framework.value.Value;
import io.basc.framework.value.ValueFactory;

public class DefaultValueFactory<K, F extends ValueFactory<K>> extends StandardObservableMap<K, Value>
		implements ConfigurableValueFactory<K> {

	@Override
	public Value get(K key) {
		Value value = super.get(key);
		return value == null ? Value.EMPTY : value;
	}

	@Override
	public void put(K key, Value value) {
		getSourceMap().put(key, value);
	}

	@Override
	public boolean putIfAbsent(K key, Value value) {
		return getSourceMap().putIfAbsent(key, value) == null;
	}
}
