package io.basc.framework.util.observe.register;

import io.basc.framework.util.Dictionary;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.observe.Registration;
import io.basc.framework.util.observe.RegistrationException;
import io.basc.framework.util.observe.Registry;

public interface KeyValueRegistry<K, V> extends Registry<KeyValue<K, V>>, Dictionary<K, V> {

	default Registration register(K key, V value) throws RegistrationException {
		return register(KeyValue.of(key, value));
	}
}
