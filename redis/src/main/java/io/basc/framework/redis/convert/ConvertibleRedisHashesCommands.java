package io.basc.framework.redis.convert;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.basc.framework.redis.RedisCodec;
import io.basc.framework.redis.RedisHashesCommands;
import io.basc.framework.util.CollectionFactory;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisHashesCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisHashesCommands<K, V> {
	RedisHashesCommands<SK, SV> getSourceRedisHashesCommands();

	@Override
	default Long hdel(K key, K... fields) {
		SK k = getKeyCodec().encode(key);
		SK[] tfs = getKeyCodec().encode(fields);
		return getSourceRedisHashesCommands().hdel(k, tfs);
	}

	@Override
	default Boolean hexists(K key, K field) {
		SK k = getKeyCodec().encode(key);
		SK f = getKeyCodec().encode(field);
		return getSourceRedisHashesCommands().hexists(k, f);
	}

	@Override
	default V hget(K key, K field) {
		SK k = getKeyCodec().encode(key);
		SK f = getKeyCodec().encode(field);
		SV v = getSourceRedisHashesCommands().hget(k, f);
		return getValueCodec().decode(v);
	}

	@Override
	default Map<K, V> hgetall(K key) {
		SK k = getKeyCodec().encode(key);
		Map<SK, SV> valueMap = getSourceRedisHashesCommands().hgetall(k);
		return CollectionFactory.convert(valueMap, getKeyCodec().toDecodeConverter(),
				getValueCodec().toDecodeConverter());
	}

	@Override
	default Long hincrby(K key, K field, long increment) {
		SK k = getKeyCodec().encode(key);
		SK f = getKeyCodec().encode(field);
		return getSourceRedisHashesCommands().hincrby(k, f, increment);
	}

	@Override
	default Double hincrbyfloat(K key, K field, double increment) {
		SK k = getKeyCodec().encode(key);
		SK f = getKeyCodec().encode(field);
		return getSourceRedisHashesCommands().hincrbyfloat(k, f, increment);
	}

	@Override
	default Set<K> hkeys(K key) {
		SK k = getKeyCodec().encode(key);
		Set<SK> tks = getSourceRedisHashesCommands().hkeys(k);
		return getKeyCodec().toDecodeConverter().convert(tks, new LinkedHashSet<K>(tks.size()));
	}

	@Override
	default Long hlen(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisHashesCommands().hlen(k);
	}

	@Override
	default List<V> hmget(K key, K... fields) {
		SK k = getKeyCodec().encode(key);
		SK[] tfs = getKeyCodec().encode(fields);
		List<SV> values = getSourceRedisHashesCommands().hmget(k, tfs);
		return getValueCodec().decode(values);
	}

	@Override
	default String hmset(K key, Map<K, V> values) {
		SK k = getKeyCodec().encode(key);
		Map<SK, SV> tMap = CollectionFactory.convert(values, getKeyCodec().toEncodeConverter(),
				getValueCodec().toEncodeConverter());
		return getSourceRedisHashesCommands().hmset(k, tMap);
	}

	@Override
	default List<K> hrandfield(K key, Integer count) {
		SK k = getKeyCodec().encode(key);
		List<SK> tks = getSourceRedisHashesCommands().hrandfield(k, count);
		return getKeyCodec().decode(tks);
	}

	@Override
	default Map<K, V> hrandfieldWithValue(K key, Integer count) {
		SK k = getKeyCodec().encode(key);
		Map<SK, SV> tMap = getSourceRedisHashesCommands().hrandfieldWithValue(k, count);
		return CollectionFactory.convert(tMap, getKeyCodec().toDecodeConverter(), getValueCodec().toDecodeConverter());
	}

	@Override
	default Long hset(K key, Map<K, V> values) {
		SK k = getKeyCodec().encode(key);
		Map<SK, SV> tMap = CollectionFactory.convert(values, getKeyCodec().toEncodeConverter(),
				getValueCodec().toEncodeConverter());
		return getSourceRedisHashesCommands().hset(k, tMap);
	}

	@Override
	default Boolean hsetnx(K key, K field, V value) {
		SK k = getKeyCodec().encode(key);
		SK tf = getKeyCodec().encode(field);
		SV tv = getValueCodec().encode(value);
		return getSourceRedisHashesCommands().hsetnx(k, tf, tv);
	}

	@Override
	default Long hstrlen(K key, K field) {
		SK k = getKeyCodec().encode(key);
		SK tf = getKeyCodec().encode(field);
		return getSourceRedisHashesCommands().hstrlen(k, tf);
	}

	@Override
	default List<V> hvals(K key) {
		SK k = getKeyCodec().encode(key);
		List<SV> values = getSourceRedisHashesCommands().hvals(k);
		return getValueCodec().decode(values);
	}
}
