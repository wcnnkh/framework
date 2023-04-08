package io.basc.framework.value.support;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import io.basc.framework.event.support.ObservableMapRegistry;
import io.basc.framework.value.ConfigurableValueFactory;
import io.basc.framework.value.Value;
import io.basc.framework.value.ValueFactory;

public class DefaultValueFactory<K, F extends ValueFactory<K>> extends ObservableMapRegistry<K, Value>
		implements ConfigurableValueFactory<K> {

	public DefaultValueFactory(Function<? super Properties, ? extends Map<K, Value>> propertiesMapper) {
		super(propertiesMapper);
	}

	@Override
	public Value get(K key) {
		Value value = super.getReadonlyMap().get(key);
		return value == null ? Value.EMPTY : value;
	}

	@Override
	public void put(K key, Value value) {
		getMaster().put(key, value);
	}

	@Override
	public boolean putIfAbsent(K key, Value value) {
		return getMaster().putIfAbsent(key, value) == null;
	}
}
