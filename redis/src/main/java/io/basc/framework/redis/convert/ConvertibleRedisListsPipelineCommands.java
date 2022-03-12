package io.basc.framework.redis.convert;

import java.util.List;

import io.basc.framework.redis.InsertPosition;
import io.basc.framework.redis.MovePosition;
import io.basc.framework.redis.RedisListsPipelineCommands;
import io.basc.framework.redis.RedisResponse;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisListsPipelineCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisListsPipelineCommands<K, V> {

	RedisListsPipelineCommands<SK, SV> getSourceRedisListsCommands();

	@Override
	default RedisResponse<V> blmove(K sourceKey, K destinationKey, MovePosition from, MovePosition to, long timout) {
		SK sk = getKeyCodec().encode(sourceKey);
		SK dk = getKeyCodec().encode(destinationKey);
		return getSourceRedisListsCommands().blmove(sk, dk, from, to, timout).map((v) -> getValueCodec().decode(v));
	}

	@Override
	default RedisResponse<List<V>> blpop(double timeout, K... keys) {
		SK[] ks = getKeyCodec().encodeAll(keys);
		return getSourceRedisListsCommands().blpop(timeout, ks).map((values) -> getValueCodec().decodeAll(values));
	}

	@Override
	default RedisResponse<List<V>> brpop(double timeout, K... keys) {
		SK[] ks = getKeyCodec().encodeAll(keys);
		return getSourceRedisListsCommands().brpop(timeout, ks).map((values) -> getValueCodec().decodeAll(values));
	}

	@Override
	default RedisResponse<V> brpoplpush(K sourceKey, K destinationKey, double timout) {
		SK sk = getKeyCodec().encode(sourceKey);
		SK dk = getKeyCodec().encode(destinationKey);
		return getSourceRedisListsCommands().brpoplpush(sk, dk, timout).map((v) -> getValueCodec().decode(v));
	}

	@Override
	default RedisResponse<V> lindex(K key, long index) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisListsCommands().lindex(k, index).map((v) -> getValueCodec().decode(v));
	}

	@Override
	default RedisResponse<Long> linsert(K key, InsertPosition position, V pivot, V value) {
		SK k = getKeyCodec().encode(key);
		SV pv = getValueCodec().encode(pivot);
		SV tv = getValueCodec().encode(value);
		return getSourceRedisListsCommands().linsert(k, position, pv, tv);
	}

	@Override
	default RedisResponse<Long> llen(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisListsCommands().llen(k);
	}

	@Override
	default RedisResponse<V> lmove(K sourceKey, K destinationKey, MovePosition from, MovePosition to) {
		SK sk = getKeyCodec().encode(sourceKey);
		SK dk = getKeyCodec().encode(destinationKey);
		return getSourceRedisListsCommands().lmove(sk, dk, from, to).map((v) -> getValueCodec().decode(v));
	}

	@Override
	default RedisResponse<List<V>> lpop(K key, int count) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisListsCommands().lpop(k, count).map((values) -> getValueCodec().decodeAll(values));
	}

	@Override
	default RedisResponse<Long> lpush(K key, V... elements) {
		SK k = getKeyCodec().encode(key);
		SV[] vs = getValueCodec().encodeAll(elements);
		return getSourceRedisListsCommands().lpush(k, vs);
	}

	@Override
	default RedisResponse<Long> lpushx(K key, V... elements) {
		SK k = getKeyCodec().encode(key);
		SV[] vs = getValueCodec().encodeAll(elements);
		return getSourceRedisListsCommands().lpushx(k, vs);
	}

	@Override
	default RedisResponse<List<V>> lrange(K key, long start, long stop) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisListsCommands().lrange(k, start, stop).map((values) -> getValueCodec().decodeAll(values));
	}

	@Override
	default RedisResponse<Long> lrem(K key, int count, V element) {
		SK k = getKeyCodec().encode(key);
		SV v = getValueCodec().encode(element);
		return getSourceRedisListsCommands().lrem(k, count, v);
	}

	@Override
	default RedisResponse<String> lset(K key, long index, V element) {
		SK k = getKeyCodec().encode(key);
		SV v = getValueCodec().encode(element);
		return getSourceRedisListsCommands().lset(k, index, v);
	}

	@Override
	default RedisResponse<String> ltrim(K key, long start, long stop) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisListsCommands().ltrim(k, start, stop);
	}

	@Override
	default RedisResponse<List<V>> rpop(K key, int count) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisListsCommands().rpop(k, count).map((values) -> getValueCodec().decodeAll(values));
	}

	@Override
	default RedisResponse<V> rpoplpush(K sourceKey, K destinationKey) {
		SK sk = getKeyCodec().encode(sourceKey);
		SK dk = getKeyCodec().encode(destinationKey);
		return getSourceRedisListsCommands().rpoplpush(sk, dk).map((v) -> getValueCodec().decode(v));
	}

	@Override
	default RedisResponse<Long> rpush(K key, V... elements) {
		SK k = getKeyCodec().encode(key);
		SV[] vs = getValueCodec().encodeAll(elements);
		return getSourceRedisListsCommands().rpush(k, vs);
	}

	@Override
	default RedisResponse<Long> rpushx(K key, V... elements) {
		SK k = getKeyCodec().encode(key);
		SV[] vs = getValueCodec().encodeAll(elements);
		return getSourceRedisListsCommands().rpushx(k, vs);
	}
}
