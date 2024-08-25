package io.basc.framework.util.register;

import io.basc.framework.util.KeyValue;

public interface KeyValueRegistration<K, V> extends Registration, KeyValue<K, V> {
	@Override
	K getKey();

	@Override
	V getValue();

	@Override
	default KeyValueRegistration<K, V> and(Registration registration) {
		if (registration == null || registration == EMPTY) {
			return this;
		}
		return new CombinableKeyValueRegistration<>(this, registration);
	}
}
