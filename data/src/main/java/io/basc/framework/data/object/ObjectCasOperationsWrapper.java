package io.basc.framework.data.object;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.codec.Codec;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.CAS;
import io.basc.framework.data.kv.KeyCasOperationsWrapper;
import io.basc.framework.util.CollectionUtils;

public interface ObjectCasOperationsWrapper<K> extends ObjectCasOperations<K>, ObjectOperationsWrapper<K>,
		KeyCasOperationsWrapper<K>, ObjectValueCasOperationsWrapper<K> {
	@Override
	ObjectCasOperations<K> getSourceOperations();

	@Override
	default boolean cas(K key, Object value, long cas) {
		return ObjectValueCasOperationsWrapper.super.cas(key, value, cas);
	}

	@Override
	default boolean cas(K key, Object value, TypeDescriptor valueType, long cas) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		Codec<Object, Object> valueFomatter = getValueFomatter();
		return getSourceOperations().cas(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), valueType, cas);
	}

	@SuppressWarnings("unchecked")
	@Override
	default <T> CAS<T> gets(TypeDescriptor type, K key) {
		Codec<K, K> keyFomatter = getKeyFomatter();
		CAS<T> value = getSourceOperations().gets(type, keyFomatter == null ? key : keyFomatter.encode(key));
		Codec<Object, Object> valueFomatter = getValueFomatter();
		return (value == null || valueFomatter == null) ? value
				: new CAS<>(value.getCas(), (T) valueFomatter.decode(value.getValue()));
	}

	@SuppressWarnings("unchecked")
	@Override
	default <T> Map<K, CAS<T>> gets(TypeDescriptor type, Collection<K> keys) {
		if (CollectionUtils.isEmpty(keys)) {
			return Collections.emptyMap();
		}

		Codec<K, K> keyFomatter = getKeyFomatter();
		Map<K, CAS<T>> map = getSourceOperations().gets(type, keyFomatter == null ? keys : keyFomatter.encode(keys));
		if (CollectionUtils.isEmpty(map)) {
			return Collections.emptyMap();
		}

		Codec<Object, Object> valueFomatter = getValueFomatter();
		Map<K, CAS<T>> targetMap = new LinkedHashMap<K, CAS<T>>(map.size());
		for (Entry<K, CAS<T>> entry : map.entrySet()) {
			CAS<T> value = entry.getValue();
			if (value == null) {
				continue;
			}

			targetMap.put(keyFomatter == null ? entry.getKey() : keyFomatter.decode(entry.getKey()),
					valueFomatter == null ? value
							: new CAS<>(value.getCas(), (T) valueFomatter.decode(value.getValue())));
		}
		return targetMap;
	}
}
