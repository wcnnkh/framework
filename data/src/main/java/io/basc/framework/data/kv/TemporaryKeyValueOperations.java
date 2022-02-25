package io.basc.framework.data.kv;

import java.util.concurrent.TimeUnit;

public interface TemporaryKeyValueOperations<K, V>
		extends TemporaryKeyOperations<K>, TemporaryValueOperations<K, V>, KeyValueOperations<K, V> {
	V getAndTouch(K key);

	V getAndTouch(K key, long exp, TimeUnit expUnit);
}
