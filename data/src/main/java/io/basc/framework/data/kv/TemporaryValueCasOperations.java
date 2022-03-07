package io.basc.framework.data.kv;

import java.util.concurrent.TimeUnit;

public interface TemporaryValueCasOperations<K, V> extends ValueCasOperations<K, V>, TemporaryValueOperations<K, V> {

	boolean cas(K key, V value, long cas, long exp, TimeUnit expUnit);

}
