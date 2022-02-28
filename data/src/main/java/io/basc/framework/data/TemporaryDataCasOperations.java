package io.basc.framework.data;

public interface TemporaryDataCasOperations<K> extends TemporaryDataOperations<K>, DataCasOperations<K>,
		TemporaryKeyValueCasOperations<K, Object>, TemporaryObjectCasOperations<K> {
}
