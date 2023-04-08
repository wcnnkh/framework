package io.basc.framework.redis.convert;

import java.util.List;
import java.util.Set;

import io.basc.framework.redis.RedisResponse;
import io.basc.framework.redis.RedisSetsPipelineCommands;
import io.basc.framework.redis.ScanOptions;
import io.basc.framework.util.page.Pageable;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisSetsPipelineCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisSetsPipelineCommands<K, V> {

	RedisSetsPipelineCommands<SK, SV> getSourceRedisSetsCommands();

	@Override
	default RedisResponse<Long> sadd(K key, V... members) {
		return getSourceRedisSetsCommands().sadd(getKeyCodec().encode(key), getValueCodec().encodeAll(members));
	}

	@Override
	default RedisResponse<Long> scard(K key) {
		return getSourceRedisSetsCommands().scard(getKeyCodec().encode(key));
	}

	@Override
	default RedisResponse<Set<V>> sdiff(K... keys) {
		return getSourceRedisSetsCommands().sdiff(getKeyCodec().encodeAll(keys))
				.map((vs) -> getValueCodec().toDecodeProcessor().processAll(vs));
	}

	@Override
	default RedisResponse<Long> sdiffstore(K destinationKey, K... keys) {
		return getSourceRedisSetsCommands().sdiffstore(getKeyCodec().encode(destinationKey),
				getKeyCodec().encodeAll(keys));
	}

	@Override
	default RedisResponse<Set<V>> sinter(K... keys) {
		return getSourceRedisSetsCommands().sinter(getKeyCodec().encodeAll(keys))
				.map((vs) -> getValueCodec().toDecodeProcessor().processAll(vs));
	}

	@Override
	default RedisResponse<Long> sinterstore(K destinationKey, K... keys) {
		return getSourceRedisSetsCommands().sinterstore(getKeyCodec().encode(destinationKey),
				getKeyCodec().encodeAll(keys));
	}

	@Override
	default RedisResponse<Boolean> sismember(K key, V member) {
		return getSourceRedisSetsCommands().sismember(getKeyCodec().encode(key), getValueCodec().encode(member));
	}

	@Override
	default RedisResponse<Set<V>> smembers(K key) {
		return getSourceRedisSetsCommands().smembers(getKeyCodec().encode(key))
				.map((vs) -> getValueCodec().toDecodeProcessor().processAll(vs));
	}

	@Override
	default RedisResponse<List<Boolean>> smismember(K key, V... members) {
		return getSourceRedisSetsCommands().smismember(getKeyCodec().encode(key), getValueCodec().encodeAll(members));
	}

	@Override
	default RedisResponse<Long> sMove(K sourceKey, K destinationKey, V member) {
		return getSourceRedisSetsCommands().sMove(getKeyCodec().encode(sourceKey), getKeyCodec().encode(destinationKey),
				getValueCodec().encode(member));
	}

	@Override
	default RedisResponse<Set<V>> spop(K key, int count) {
		return getSourceRedisSetsCommands().spop(getKeyCodec().encode(key), count)
				.map((vs) -> getValueCodec().toDecodeProcessor().processAll(vs));
	}

	@Override
	default RedisResponse<List<V>> srandmember(K key, int count) {
		return getSourceRedisSetsCommands().srandmember(getKeyCodec().encode(key), count)
				.map((vs) -> getValueCodec().decodeAll(vs));
	}

	@Override
	default RedisResponse<Long> srem(K key, V... members) {
		return getSourceRedisSetsCommands().srem(getKeyCodec().encode(key), getValueCodec().encodeAll(members));
	}

	@Override
	default RedisResponse<Set<V>> sunion(K... keys) {
		return getSourceRedisSetsCommands().sunion(getKeyCodec().encodeAll(keys))
				.map((vs) -> getValueCodec().toDecodeProcessor().processAll(vs));
	}

	@Override
	default RedisResponse<Long> sunionstore(K destinationKey, K... keys) {
		return getSourceRedisSetsCommands().sunionstore(getKeyCodec().encode(destinationKey),
				getKeyCodec().encodeAll(keys));
	}

	@Override
	default RedisResponse<Pageable<Long, K>> sScan(long cursorId, K key, ScanOptions<K> options) {
		RedisResponse<Pageable<Long, SK>> response = getSourceRedisSetsCommands().sScan(cursorId,
				getKeyCodec().encode(key), options.convert(getKeyCodec().toEncodeProcessor()));
		return response.map((p) -> p.map((v) -> getKeyCodec().decode(v)));
	}
}
