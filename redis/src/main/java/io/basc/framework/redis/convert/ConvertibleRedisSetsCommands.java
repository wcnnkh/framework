package io.basc.framework.redis.convert;

import java.util.List;
import java.util.Set;

import io.basc.framework.redis.RedisCodec;
import io.basc.framework.redis.RedisSetsCommands;
import io.basc.framework.redis.ScanOptions;
import io.basc.framework.util.page.Pageable;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisSetsCommands<SK, K, SV, V> extends RedisCodec<SK, K, SV, V>, RedisSetsCommands<K, V> {

	RedisSetsCommands<SK, SV> getSourceRedisSetsCommands();

	@Override
	default Long sadd(K key, V... members) {
		return getSourceRedisSetsCommands().sadd(getKeyCodec().encode(key), getValueCodec().encode(members));
	}

	@Override
	default Long scard(K key) {
		return getSourceRedisSetsCommands().scard(getKeyCodec().encode(key));
	}

	@Override
	default Set<V> sdiff(K... keys) {
		Set<SV> vs = getSourceRedisSetsCommands().sdiff(getKeyCodec().encode(keys));
		return getValueCodec().toDecodeConverter().convert(vs);
	}

	@Override
	default Long sdiffstore(K destinationKey, K... keys) {
		return getSourceRedisSetsCommands().sdiffstore(getKeyCodec().encode(destinationKey),
				getKeyCodec().encode(keys));
	}

	@Override
	default Set<V> sinter(K... keys) {
		Set<SV> vs = getSourceRedisSetsCommands().sinter(getKeyCodec().encode(keys));
		return getValueCodec().toDecodeConverter().convert(vs);
	}

	@Override
	default Long sinterstore(K destinationKey, K... keys) {
		return getSourceRedisSetsCommands().sinterstore(getKeyCodec().encode(destinationKey),
				getKeyCodec().encode(keys));
	}

	@Override
	default Boolean sismember(K key, V member) {
		return getSourceRedisSetsCommands().sismember(getKeyCodec().encode(key), getValueCodec().encode(member));
	}

	@Override
	default Set<V> smembers(K key) {
		Set<SV> vs = getSourceRedisSetsCommands().smembers(getKeyCodec().encode(key));
		return getValueCodec().toDecodeConverter().convert(vs);
	}

	@Override
	default List<Boolean> smismember(K key, V... members) {
		return getSourceRedisSetsCommands().smismember(getKeyCodec().encode(key), getValueCodec().encode(members));
	}

	@Override
	default Boolean sMove(K sourceKey, K destinationKey, V member) {
		return getSourceRedisSetsCommands().sMove(getKeyCodec().encode(sourceKey), getKeyCodec().encode(destinationKey),
				getValueCodec().encode(member));
	}

	@Override
	default Set<V> spop(K key, int count) {
		Set<SV> vs = getSourceRedisSetsCommands().spop(getKeyCodec().encode(key), count);
		return getValueCodec().toDecodeConverter().convert(vs);
	}

	@Override
	default List<V> srandmember(K key, int count) {
		List<SV> vs = getSourceRedisSetsCommands().srandmember(getKeyCodec().encode(key), count);
		return getValueCodec().decode(vs);
	}

	@Override
	default Long srem(K key, V... members) {
		return getSourceRedisSetsCommands().srem(getKeyCodec().encode(key), getValueCodec().encode(members));
	}

	@Override
	default Set<V> sunion(K... keys) {
		Set<SV> vs = getSourceRedisSetsCommands().sunion(getKeyCodec().encode(keys));
		return getValueCodec().toDecodeConverter().convert(vs);
	}

	@Override
	default Long sunionstore(K destinationKey, K... keys) {
		return getSourceRedisSetsCommands().sunionstore(getKeyCodec().encode(destinationKey),
				getKeyCodec().encode(keys));
	}

	@Override
	default Pageable<Long, K> sScan(long cursorId, K key, ScanOptions<K> options) {
		return getSourceRedisSetsCommands()
				.sScan(cursorId, getKeyCodec().encode(key), options.convert(getKeyCodec().toEncodeConverter()))
				.map((v) -> getKeyCodec().decode(v));
	}
}
