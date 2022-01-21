package io.basc.framework.redis.convert;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.basc.framework.redis.RedisCodec;
import io.basc.framework.redis.RedisHashesPipelineCommands;
import io.basc.framework.redis.RedisResponse;
import io.basc.framework.util.CollectionFactory;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisHashesPipelineCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisHashesPipelineCommands<K, V> {

	RedisHashesPipelineCommands<SK, SV> getSourceRedisHashesCommands();

	@Override
	default RedisResponse<Long> hdel(K key, K... fields) {
		SK k = getKeyCodec().encode(key);
		SK[] tfs = getKeyCodec().encode(fields);
		return getSourceRedisHashesCommands().hdel(k, tfs);
	}

	@Override
	default RedisResponse<Boolean> hexists(K key, K field) {
		SK k = getKeyCodec().encode(key);
		SK f = getKeyCodec().encode(field);
		return getSourceRedisHashesCommands().hexists(k, f);
	}

	@Override
	default RedisResponse<V> hget(K key, K field) {
		SK k = getKeyCodec().encode(key);
		SK f = getKeyCodec().encode(field);
		return getSourceRedisHashesCommands().hget(k, f).map((v) -> getValueCodec().decode(v));
	}

	@Override
	default RedisResponse<Map<K, V>> hgetall(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisHashesCommands().hgetall(k).map((valueMap) -> CollectionFactory.convert(valueMap,
				getKeyCodec().toDecodeConverter(), getValueCodec().toDecodeConverter()));
	}

	@Override
	default RedisResponse<Long> hincrby(K key, K field, long increment) {
		SK k = getKeyCodec().encode(key);
		SK f = getKeyCodec().encode(field);
		return getSourceRedisHashesCommands().hincrby(k, f, increment);
	}

	@Override
	default RedisResponse<Double> hincrbyfloat(K key, K field, double increment) {
		SK k = getKeyCodec().encode(key);
		SK f = getKeyCodec().encode(field);
		return getSourceRedisHashesCommands().hincrbyfloat(k, f, increment);
	}

	@Override
	default RedisResponse<Set<K>> hkeys(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisHashesCommands().hkeys(k)
				.map((tks) -> getKeyCodec().toDecodeConverter().convert(tks, new LinkedHashSet<K>(tks.size())));
	}

	@Override
	default RedisResponse<Long> hlen(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisHashesCommands().hlen(k);
	}

	@Override
	default RedisResponse<List<V>> hmget(K key, K... fields) {
		SK k = getKeyCodec().encode(key);
		SK[] tfs = getKeyCodec().encode(fields);
		return getSourceRedisHashesCommands().hmget(k, tfs).map((values) -> getValueCodec().decode(values));
	}

	@Override
	default RedisResponse<String> hmset(K key, Map<K, V> values) {
		SK k = getKeyCodec().encode(key);
		Map<SK, SV> tMap = CollectionFactory.convert(values, getKeyCodec().toEncodeConverter(),
				getValueCodec().toEncodeConverter());
		return getSourceRedisHashesCommands().hmset(k, tMap);
	}

	@Override
	default RedisResponse<List<K>> hrandfield(K key, Integer count) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisHashesCommands().hrandfield(k, count).map((tks) -> getKeyCodec().decode(tks));
	}

	@Override
	default RedisResponse<Map<K, V>> hrandfieldWithValue(K key, Integer count) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisHashesCommands().hrandfieldWithValue(k, count).map((tMap) -> CollectionFactory
				.convert(tMap, getKeyCodec().toDecodeConverter(), getValueCodec().toDecodeConverter()));
	}

	@Override
	default RedisResponse<Long> hset(K key, Map<K, V> values) {
		SK k = getKeyCodec().encode(key);
		Map<SK, SV> tMap = CollectionFactory.convert(values, getKeyCodec().toEncodeConverter(),
				getValueCodec().toEncodeConverter());
		return getSourceRedisHashesCommands().hset(k, tMap);
	}

	@Override
	default RedisResponse<Boolean> hsetnx(K key, K field, V value) {
		SK k = getKeyCodec().encode(key);
		SK tf = getKeyCodec().encode(field);
		SV tv = getValueCodec().encode(value);
		return getSourceRedisHashesCommands().hsetnx(k, tf, tv);
	}

	@Override
	default RedisResponse<Long> hstrlen(K key, K field) {
		SK k = getKeyCodec().encode(key);
		SK tf = getKeyCodec().encode(field);
		return getSourceRedisHashesCommands().hstrlen(k, tf);
	}

	@Override
	default RedisResponse<List<V>> hvals(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisHashesCommands().hvals(k).map((values) -> getValueCodec().decode(values));
	}
}
