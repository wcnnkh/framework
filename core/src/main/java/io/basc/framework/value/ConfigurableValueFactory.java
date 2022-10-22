package io.basc.framework.value;

import io.basc.framework.util.Assert;

public interface ConfigurableValueFactory<K> extends ValueFactory<K> {

	default void put(K key, Object value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		put(key, Value.of(value));
	}

	default boolean putIfAbsent(K key, Object value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return putIfAbsent(key, Value.of(value));
	}

	void put(K key, Value value);

	boolean putIfAbsent(K key, Value value);
}
