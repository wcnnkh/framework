package run.soeasy.framework.core.exchange.container;

import java.util.Arrays;

import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.collection.KeyValues;
import run.soeasy.framework.core.exchange.Receipt;
import run.soeasy.framework.core.exchange.Registration;

public interface KeyValueRegistry<K, V> extends Registry<KeyValue<K, V>>, KeyValues<K, V> {

	default Registration register(K key, V value) throws RegistrationException {
		return register(KeyValue.of(key, value));
	}
	
	default Receipt deregisterKey(K key) {
		return deregisterKeys(Arrays.asList(key));
	}

	/**
	 * 
	 * @param keys
	 * @return 有一个成功就是成功
	 */
	Receipt deregisterKeys(Iterable<? extends K> keys);
}
