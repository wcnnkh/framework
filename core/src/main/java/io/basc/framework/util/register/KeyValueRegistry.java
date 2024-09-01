package io.basc.framework.util.register;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.match.Matcher;

public interface KeyValueRegistry<K, V> extends Registry<KeyValue<K, V>> {

	default Registration register(K key, V value) throws RegistrationException {
		return register(KeyValue.of(key, value));
	}

	default Elements<KeyValue<K, V>> getElements(K key, Matcher<? super K> matcher) {
		return getElements().filter((e) -> matcher.match(key, e.getKey()));
	}
}
