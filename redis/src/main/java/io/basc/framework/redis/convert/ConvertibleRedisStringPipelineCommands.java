package io.basc.framework.redis.convert;

import java.util.List;
import java.util.Map;

import io.basc.framework.core.convert.transform.stractegy.CollectionFactory;
import io.basc.framework.redis.BitOP;
import io.basc.framework.redis.ExpireOption;
import io.basc.framework.redis.RedisResponse;
import io.basc.framework.redis.RedisStringPipelineCommands;
import io.basc.framework.redis.SetOption;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisStringPipelineCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisStringPipelineCommands<K, V> {

	RedisStringPipelineCommands<SK, SV> getSourceRedisStringCommands();

	@Override
	default RedisResponse<Long> append(K key, V value) {
		return getSourceRedisStringCommands().append(getKeyCodec().encode(key), getValueCodec().encode(value));
	}

	@Override
	default RedisResponse<Long> bitcount(K key, long start, long end) {
		return getSourceRedisStringCommands().bitcount(getKeyCodec().encode(key), start, end);
	}

	@Override
	default RedisResponse<Long> bitop(BitOP op, K destkey, K... srcKeys) {
		return getSourceRedisStringCommands().bitop(op, getKeyCodec().encode(destkey), getKeyCodec().encodeAll(srcKeys));
	}

	@Override
	default RedisResponse<Long> bitpos(K key, boolean bit, Long start, Long end) {
		return getSourceRedisStringCommands().bitpos(getKeyCodec().encode(key), bit, start, end);
	}

	@Override
	default RedisResponse<Long> decr(K key) {
		return getSourceRedisStringCommands().decr(getKeyCodec().encode(key));
	}

	@Override
	default RedisResponse<Long> decrBy(K key, long decrement) {
		return getSourceRedisStringCommands().decrBy(getKeyCodec().encode(key), decrement);
	}

	@Override
	default RedisResponse<V> get(K key) {
		return getSourceRedisStringCommands().get(getKeyCodec().encode(key)).map((v) -> getValueCodec().decode(v));
	}

	@Override
	default RedisResponse<Boolean> getbit(K key, Long offset) {
		return getSourceRedisStringCommands().getbit(getKeyCodec().encode(key), offset);
	}

	@Override
	default RedisResponse<V> getdel(K key) {
		return getSourceRedisStringCommands().getdel(getKeyCodec().encode(key)).map((v) -> getValueCodec().decode(v));
	}

	@Override
	default RedisResponse<V> getEx(K key, ExpireOption option, Long time) {
		return getSourceRedisStringCommands().getEx(getKeyCodec().encode(key), option, time)
				.map((v) -> getValueCodec().decode(v));
	}

	@Override
	default RedisResponse<V> getrange(K key, long startOffset, long endOffset) {
		return getSourceRedisStringCommands().getrange(getKeyCodec().encode(key), startOffset, endOffset)
				.map((v) -> getValueCodec().decode(v));
	}

	@Override
	default RedisResponse<V> getset(K key, V value) {
		return getSourceRedisStringCommands().getset(getKeyCodec().encode(key), getValueCodec().encode(value))
				.map((v) -> getValueCodec().decode(v));
	}

	@Override
	default RedisResponse<Long> incr(K key) {
		return getSourceRedisStringCommands().incr(getKeyCodec().encode(key));
	}

	@Override
	default RedisResponse<Long> incrBy(K key, long increment) {
		return getSourceRedisStringCommands().incrBy(getKeyCodec().encode(key), increment);
	}

	@Override
	default RedisResponse<Double> incrByFloat(K key, double increment) {
		return getSourceRedisStringCommands().incrByFloat(getKeyCodec().encode(key), increment);
	}

	@Override
	default RedisResponse<List<V>> mget(K... keys) {
		return getSourceRedisStringCommands().mget(getKeyCodec().encodeAll(keys))
				.map((values) -> getValueCodec().decodeAll(values));
	}

	@Override
	default RedisResponse<String> mset(Map<K, V> pairs) {
		return getSourceRedisStringCommands().mset(CollectionFactory.convert(pairs, getKeyCodec().toEncodeProcessor(),
				getValueCodec().toEncodeProcessor()));
	}

	@Override
	default RedisResponse<Long> msetnx(Map<K, V> pairs) {
		return getSourceRedisStringCommands().msetnx(CollectionFactory.convert(pairs, getKeyCodec().toEncodeProcessor(),
				getValueCodec().toEncodeProcessor()));
	}

	@Override
	default RedisResponse<String> psetex(K key, long milliseconds, V value) {
		return getSourceRedisStringCommands().psetex(getKeyCodec().encode(key), milliseconds,
				getValueCodec().encode(value));
	}

	@Override
	default RedisResponse<String> set(K key, V value) {
		return getSourceRedisStringCommands().set(getKeyCodec().encode(key), getValueCodec().encode(value));
	}

	@Override
	default RedisResponse<String> set(K key, V value, ExpireOption option, long time, SetOption setOption) {
		return getSourceRedisStringCommands().set(getKeyCodec().encode(key), getValueCodec().encode(value), option,
				time, setOption);
	}

	@Override
	default RedisResponse<Boolean> setbit(K key, long offset, boolean value) {
		return getSourceRedisStringCommands().setbit(getKeyCodec().encode(key), offset, value);
	}

	@Override
	default RedisResponse<String> setex(K key, long seconds, V value) {
		return getSourceRedisStringCommands().setex(getKeyCodec().encode(key), seconds, getValueCodec().encode(value));
	}

	@Override
	default RedisResponse<Long> setNX(K key, V value) {
		return getSourceRedisStringCommands().setNX(getKeyCodec().encode(key), getValueCodec().encode(value));
	}

	@Override
	default RedisResponse<Long> setrange(K key, Long offset, V value) {
		return getSourceRedisStringCommands().setrange(getKeyCodec().encode(key), offset,
				getValueCodec().encode(value));
	}

	@Override
	default RedisResponse<Long> strlen(K key) {
		return getSourceRedisStringCommands().strlen(getKeyCodec().encode(key));
	}
}
