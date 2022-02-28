package io.basc.framework.data;

public interface TemporaryDataOperations<K>
		extends DataOperations<K>, TemporaryKeyValueOperations<K, Object>, TemporaryObjectOperations<K> {
}
