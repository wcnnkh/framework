package io.basc.framework.util.register;

import io.basc.framework.util.KeyValue;
import io.basc.framework.util.KeyValues;
import io.basc.framework.util.Registration;

public interface KeyValueRegistry<K, V> extends Registry<KeyValue<K, V>>, KeyValues<K, V> {

	default Registration register(K key, V value) throws RegistrationException {
		return register(KeyValue.of(key, value));
	}
}
