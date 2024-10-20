package io.basc.framework.util;

public interface KeyValuesWrapper<K, V>
		extends KeyValues<K, V>, ElementsWrapper<KeyValue<K, V>, Elements<KeyValue<K, V>>> {
}
