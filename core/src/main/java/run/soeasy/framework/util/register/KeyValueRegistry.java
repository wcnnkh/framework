package run.soeasy.framework.util.register;

import java.util.Arrays;

import run.soeasy.framework.util.KeyValue;
import run.soeasy.framework.util.collection.KeyValues;
import run.soeasy.framework.util.exchange.Receipt;
import run.soeasy.framework.util.exchange.Registration;

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
