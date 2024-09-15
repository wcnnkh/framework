package io.basc.framework.util.register;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.Registration;

public interface KeyValueRegistration<K, V> extends PayloadRegistration<KeyValue<K, V>>, KeyValue<K, V> {
	@Override
	K getKey();

	@Override
	V getValue();

	@Override
	default KeyValue<K, V> getPayload() {
		return KeyValue.of(getKey(), getValue());
	}

	@Override
	default KeyValueRegistration<K, V> and(Registration registration) {
		if (registration == null || registration.isCancelled()) {
			return this;
		}
		return new StandardKeyValueRegistrationWrapper<>(this, Elements.singleton(registration));
	}
}
