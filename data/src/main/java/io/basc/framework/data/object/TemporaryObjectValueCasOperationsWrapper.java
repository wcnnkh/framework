package io.basc.framework.data.object;

import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.Encoder;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.kv.TemporaryValueCasOperationsWrapper;

public interface TemporaryObjectValueCasOperationsWrapper<K>
		extends TemporaryObjectValueCasOperations<K>, TemporaryObjectValueOperationsWrapper<K>,
		ObjectValueCasOperationsWrapper<K>, TemporaryValueCasOperationsWrapper<K, Object> {
	@Override
	TemporaryObjectValueCasOperations<K> getSourceOperations();

	@Override
	default boolean cas(K key, Object value, TypeDescriptor valueType, long cas) {
		return ObjectValueCasOperationsWrapper.super.cas(key, value, valueType, cas);
	}

	@Override
	default boolean cas(K key, Object value, long cas, long exp, TimeUnit expUnit) {
		return TemporaryValueCasOperationsWrapper.super.cas(key, value, cas, exp, expUnit);
	}

	@Override
	default boolean cas(K key, Object value, long cas) {
		return ObjectValueCasOperationsWrapper.super.cas(key, value, cas);
	}
	
	@Override
	default boolean cas(K key, Object value, TypeDescriptor valueType, long cas, long exp, TimeUnit expUnit) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<Object, Object> valueFomatter = getValueFomatter();
		return cas(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), valueType, cas, exp, expUnit);
	}
}
