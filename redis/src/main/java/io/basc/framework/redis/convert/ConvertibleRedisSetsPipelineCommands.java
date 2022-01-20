package io.basc.framework.redis.convert;

import java.util.List;
import java.util.Set;

import io.basc.framework.redis.RedisCodec;
import io.basc.framework.redis.RedisResponse;
import io.basc.framework.redis.RedisSetsPipelineCommands;
import io.basc.framework.redis.ScanOptions;
import io.basc.framework.util.page.Pageable;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisSetsPipelineCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisSetsPipelineCommands<K, V> {

	RedisSetsPipelineCommands<SK, SV> getSourceRedisSetsPipelineCommands();

	@Override
	default RedisResponse<Long> sadd(K key, V... members) {
		return getSourceRedisSetsPipelineCommands().sadd(getKeyCodec().encode(key), getValueCodec().encode(members));
	}

	@Override
	default RedisResponse<Long> scard(K key) {
		return getSourceRedisSetsPipelineCommands().scard(getKeyCodec().encode(key));
	}

	@Override
	default RedisResponse<Set<V>> sdiff(K... keys) {
		return getSourceRedisSetsPipelineCommands().sdiff(getKeyCodec().encode(keys))
				.map((vs) -> getValueCodec().toDecodeConverter().convert(vs));
	}

	@Override
	default RedisResponse<Long> sdiffstore(K destinationKey, K... keys) {
		return getSourceRedisSetsPipelineCommands().sdiffstore(getKeyCodec().encode(destinationKey),
				getKeyCodec().encode(keys));
	}

	@Override
	default RedisResponse<Set<V>> sinter(K... keys) {
		return getSourceRedisSetsPipelineCommands().sinter(getKeyCodec().encode(keys))
				.map((vs) -> getValueCodec().toDecodeConverter().convert(vs));
	}

	@Override
	default RedisResponse<Long> sinterstore(K destinationKey, K... keys) {
		return getSourceRedisSetsPipelineCommands().sinterstore(getKeyCodec().encode(destinationKey),
				getKeyCodec().encode(keys));
	}

	@Override
	default RedisResponse<Boolean> sismember(K key, V member) {
		return getSourceRedisSetsPipelineCommands().sismember(getKeyCodec().encode(key),
				getValueCodec().encode(member));
	}

	@Override
	default RedisResponse<Set<V>> smembers(K key) {
		return getSourceRedisSetsPipelineCommands().smembers(getKeyCodec().encode(key))
				.map((vs) -> getValueCodec().toDecodeConverter().convert(vs));
	}

	@Override
	default RedisResponse<List<Boolean>> smismember(K key, V... members) {
		return getSourceRedisSetsPipelineCommands().smismember(getKeyCodec().encode(key),
				getValueCodec().encode(members));
	}

	@Override
	default RedisResponse<Long> sMove(K sourceKey, K destinationKey, V member) {
		return getSourceRedisSetsPipelineCommands().sMove(getKeyCodec().encode(sourceKey),
				getKeyCodec().encode(destinationKey), getValueCodec().encode(member));
	}

	@Override
	default RedisResponse<Set<V>> spop(K key, int count) {
		return getSourceRedisSetsPipelineCommands().spop(getKeyCodec().encode(key), count)
				.map((vs) -> getValueCodec().toDecodeConverter().convert(vs));
	}

	@Override
	default RedisResponse<List<V>> srandmember(K key, int count) {
		return getSourceRedisSetsPipelineCommands().srandmember(getKeyCodec().encode(key), count)
				.map((vs) -> getValueCodec().decode(vs));
	}

	@Override
	default RedisResponse<Long> srem(K key, V... members) {
		return getSourceRedisSetsPipelineCommands().srem(getKeyCodec().encode(key), getValueCodec().encode(members));
	}

	@Override
	default RedisResponse<Set<V>> sunion(K... keys) {
		return getSourceRedisSetsPipelineCommands().sunion(getKeyCodec().encode(keys))
				.map((vs) -> getValueCodec().toDecodeConverter().convert(vs));
	}

	@Override
	default RedisResponse<Long> sunionstore(K destinationKey, K... keys) {
		return getSourceRedisSetsPipelineCommands().sunionstore(getKeyCodec().encode(destinationKey),
				getKeyCodec().encode(keys));
	}

	@Override
	default RedisResponse<Pageable<Long, K>> sScan(long cursorId, K key, ScanOptions<K> options) {
		return getSourceRedisSetsPipelineCommands()
				.sScan(cursorId, getKeyCodec().encode(key), options.convert(getKeyCodec().toEncodeConverter()))
				.map((p) -> p.map((v) -> getKeyCodec().decode(v)));
	}
}
