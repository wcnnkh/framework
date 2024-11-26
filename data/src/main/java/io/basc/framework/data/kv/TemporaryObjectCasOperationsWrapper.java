package io.basc.framework.data.kv;

import java.util.concurrent.TimeUnit;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.codec.Encoder;

public interface TemporaryObjectCasOperationsWrapper<K> extends TemporaryObjectCasOperations<K>,
		TemporaryObjectOperationsWrapper<K>, TemporaryKeyValueCasOperations<K, Object>, ObjectCasOperationsWrapper<K> {
	@Override
	TemporaryObjectCasOperations<K> getSourceOperations();

	@Override
	default boolean cas(K key, Object value, TypeDescriptor valueType, long cas, long exp, TimeUnit expUnit) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<Object, Object> valueFomatter = getValueFomatter();
		return cas(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), valueType, cas, exp, expUnit);
	}
}
