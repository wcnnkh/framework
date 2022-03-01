package io.basc.framework.data.object;

import io.basc.framework.codec.Encoder;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.ValueCasOperationsWrapper;

public interface ObjectValueCasOperationsWrapper<K>
		extends ObjectValueCasOperations<K>, ObjectValueOperationsWrapper<K>, ValueCasOperationsWrapper<K, Object> {
	ObjectValueCasOperations<K> getSourceOperations();

	@Override
	default boolean cas(K key, Object value, long cas) {
		return ValueCasOperationsWrapper.super.cas(key, value, cas);
	}

	@Override
	default boolean cas(K key, Object value, TypeDescriptor valueType, long cas) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<Object, Object> valueFomatter = getValueFomatter();
		return cas(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), valueType, cas);
	}
}
