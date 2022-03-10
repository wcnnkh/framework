package io.basc.framework.data.kv;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.Encoder;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.CollectionUtils;

public interface ObjectOperationsWrapper<K> extends ObjectOperations<K>, KeyValueOperationsWrapper<K, Object> {

	@Override
	ObjectOperations<K> getSourceOperations();

	@Override
	default void set(K key, Object value, TypeDescriptor valueType) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<Object, Object> valueFomatter = getValueFomatter();
		getSourceOperations().set(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), valueType);
	}

	@Override
	default boolean setIfAbsent(K key, Object value, TypeDescriptor valueType) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<Object, Object> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfAbsent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), valueType);
	}

	@Override
	default boolean setIfPresent(K key, Object value, TypeDescriptor valueType) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Encoder<Object, Object> valueFomatter = getValueFomatter();
		return getSourceOperations().setIfPresent(keyFomatter == null ? key : keyFomatter.encode(key),
				valueFomatter == null ? value : valueFomatter.encode(value), valueType);
	}

	@SuppressWarnings("unchecked")
	@Override
	default <T> T get(TypeDescriptor type, K key) {
		Encoder<K, K> keyFomatter = getKeyFomatter();
		Object value = getSourceOperations().get(type, keyFomatter == null ? key : keyFomatter.encode(key));
		Codec<Object, Object> valueFomatter = getValueFomatter();
		return (T) (valueFomatter == null ? value : valueFomatter.decode(value));
	}

	@SuppressWarnings("unchecked")
	@Override
	default <T> Map<K, T> get(TypeDescriptor type, Collection<K> keys) {
		if (CollectionUtils.isEmpty(keys)) {
			return Collections.emptyMap();
		}

		Codec<K, K> keyFomatter = getKeyFomatter();
		Map<K, T> map = getSourceOperations().get(type, keyFomatter == null ? keys : keyFomatter.encode(keys));
		if (CollectionUtils.isEmpty(map)) {
			return Collections.emptyMap();
		}

		Codec<Object, Object> valueFomatter = getValueFomatter();
		Map<K, T> targetMap = new LinkedHashMap<K, T>(map.size());
		for (Entry<K, T> entry : map.entrySet()) {
			targetMap.put(keyFomatter == null ? entry.getKey() : keyFomatter.decode(entry.getKey()),
					valueFomatter == null ? entry.getValue() : (T) valueFomatter.decode(entry.getValue()));
		}
		return targetMap;
	}

	@Override
	default boolean setIfAbsent(K key, Object value) {
		return KeyValueOperationsWrapper.super.setIfAbsent(key, value);
	}

	@Override
	default boolean setIfPresent(K key, Object value) {
		return KeyValueOperationsWrapper.super.setIfPresent(key, value);
	}

	@Override
	default void set(K key, Object value) {
		KeyValueOperationsWrapper.super.set(key, value);
	}
}
