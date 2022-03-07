package io.basc.framework.data;

import java.util.concurrent.TimeUnit;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.TemporaryKeyValueCasOperations;
import io.basc.framework.data.object.TemporaryObjectCasOperations;

public interface TemporaryDataCasOperations<K> extends TemporaryDataOperations<K>, DataCasOperations<K>,
		TemporaryKeyValueCasOperations<K, Object>, TemporaryObjectCasOperations<K> {

	@Override
	default boolean cas(K key, Object value, long cas, long exp, TimeUnit expUnit) {
		return cas(key, value, TypeDescriptor.forObject(value), cas, exp, expUnit);
	}
}
