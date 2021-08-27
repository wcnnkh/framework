package io.basc.framework.value;

import io.basc.framework.core.Assert;

public interface ConfigurableValueFactory<K> extends ValueFactory<K> {
	boolean put(K key, Value value);

	boolean putIfAbsent(K key, Value value);

	boolean remove(K key);

	default boolean put(K key, Object value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return put(key, (value instanceof Value) ? ((Value) value) : new AnyValue(value));
	}

	default boolean putIfAbsent(K key, Object value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return putIfAbsent(key, (value instanceof Value) ? ((Value) value) : new AnyValue(value));
	}
}
