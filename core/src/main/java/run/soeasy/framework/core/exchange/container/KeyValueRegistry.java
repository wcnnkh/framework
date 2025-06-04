package run.soeasy.framework.core.exchange.container;

import java.util.Arrays;

import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.collection.KeyValues;
import run.soeasy.framework.core.domain.KeyValue;
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

	@Override
	default Elements<K> keys() {
		return map((e) -> e.getKey());
	}

	@Override
	default Elements<V> getValues(K key) {
		return filter((e) -> ObjectUtils.equals(key, e.getKey())).map((e) -> e.getValue());
	}
}
