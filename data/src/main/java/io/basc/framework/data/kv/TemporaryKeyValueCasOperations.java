package io.basc.framework.data.kv;

public interface TemporaryKeyValueCasOperations<K, V> extends KeyValueCasOperations<K, V>,
		TemporaryKeyValueOperations<K, V>, TemporaryKeyCasOperations<K>, TemporaryValueCasOperations<K, V> {

}
