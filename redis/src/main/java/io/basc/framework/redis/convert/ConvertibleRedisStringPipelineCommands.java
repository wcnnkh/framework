package io.basc.framework.redis.convert;

import java.util.List;
import java.util.Map;

import io.basc.framework.redis.BitOP;
import io.basc.framework.redis.ExpireOption;
import io.basc.framework.redis.RedisCodec;
import io.basc.framework.redis.RedisResponse;
import io.basc.framework.redis.RedisStringPipelineCommands;
import io.basc.framework.redis.SetOption;
import io.basc.framework.util.CollectionFactory;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisStringPipelineCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisStringPipelineCommands<K, V> {

	RedisStringPipelineCommands<SK, SV> getSourceRedisStringPipelineCommands();

	@Override
	default RedisResponse<Long> append(K key, V value) {
		return getSourceRedisStringPipelineCommands().append(getKeyCodec().encode(key), getValueCodec().encode(value));
	}

	@Override
	default RedisResponse<Long> bitcount(K key, long start, long end) {
		return getSourceRedisStringPipelineCommands().bitcount(getKeyCodec().encode(key), start, end);
	}

	@Override
	default RedisResponse<Long> bitop(BitOP op, K destkey, K... srcKeys) {
		return getSourceRedisStringPipelineCommands().bitop(op, getKeyCodec().encode(destkey),
				getKeyCodec().encode(srcKeys));
	}

	@Override
	default RedisResponse<Long> bitpos(K key, boolean bit, Long start, Long end) {
		return getSourceRedisStringPipelineCommands().bitpos(getKeyCodec().encode(key), bit, start, end);
	}

	@Override
	default RedisResponse<Long> decr(K key) {
		return getSourceRedisStringPipelineCommands().decr(getKeyCodec().encode(key));
	}

	@Override
	default RedisResponse<Long> decrBy(K key, long decrement) {
		return getSourceRedisStringPipelineCommands().decrBy(getKeyCodec().encode(key), decrement);
	}

	@Override
	default RedisResponse<V> get(K key) {
		return getSourceRedisStringPipelineCommands().get(getKeyCodec().encode(key))
				.map((v) -> getValueCodec().decode(v));
	}

	@Override
	default RedisResponse<Boolean> getbit(K key, Long offset) {
		return getSourceRedisStringPipelineCommands().getbit(getKeyCodec().encode(key), offset);
	}

	@Override
	default RedisResponse<V> getdel(K key) {
		return getSourceRedisStringPipelineCommands().getdel(getKeyCodec().encode(key))
				.map((v) -> getValueCodec().decode(v));
	}

	@Override
	default RedisResponse<V> getEx(K key, ExpireOption option, Long time) {
		return getSourceRedisStringPipelineCommands().getEx(getKeyCodec().encode(key), option, time)
				.map((v) -> getValueCodec().decode(v));
	}

	@Override
	default RedisResponse<V> getrange(K key, long startOffset, long endOffset) {
		return getSourceRedisStringPipelineCommands().getrange(getKeyCodec().encode(key), startOffset, endOffset)
				.map((v) -> getValueCodec().decode(v));
	}

	@Override
	default RedisResponse<V> getset(K key, V value) {
		return getSourceRedisStringPipelineCommands().getset(getKeyCodec().encode(key), getValueCodec().encode(value))
				.map((v) -> getValueCodec().decode(v));
	}

	@Override
	default RedisResponse<Long> incr(K key) {
		return getSourceRedisStringPipelineCommands().incr(getKeyCodec().encode(key));
	}

	@Override
	default RedisResponse<Long> incrBy(K key, long increment) {
		return getSourceRedisStringPipelineCommands().incrBy(getKeyCodec().encode(key), increment);
	}

	@Override
	default RedisResponse<Double> incrByFloat(K key, double increment) {
		return getSourceRedisStringPipelineCommands().incrByFloat(getKeyCodec().encode(key), increment);
	}

	@Override
	default RedisResponse<List<V>> mget(K... keys) {
		return getSourceRedisStringPipelineCommands().mget(getKeyCodec().encode(keys))
				.map((values) -> getValueCodec().decode(values));
	}

	@Override
	default RedisResponse<String> mset(Map<K, V> pairs) {
		return getSourceRedisStringPipelineCommands().mset(CollectionFactory.convert(pairs,
				getKeyCodec().toEncodeConverter(), getValueCodec().toEncodeConverter()));
	}

	@Override
	default RedisResponse<Long> msetnx(Map<K, V> pairs) {
		return getSourceRedisStringPipelineCommands().msetnx(CollectionFactory.convert(pairs,
				getKeyCodec().toEncodeConverter(), getValueCodec().toEncodeConverter()));
	}

	@Override
	default RedisResponse<String> psetex(K key, long milliseconds, V value) {
		return getSourceRedisStringPipelineCommands().psetex(getKeyCodec().encode(key), milliseconds,
				getValueCodec().encode(value));
	}

	@Override
	default RedisResponse<String> set(K key, V value) {
		return getSourceRedisStringPipelineCommands().set(getKeyCodec().encode(key), getValueCodec().encode(value));
	}

	@Override
	default RedisResponse<String> set(K key, V value, ExpireOption option, long time, SetOption setOption) {
		return getSourceRedisStringPipelineCommands().set(getKeyCodec().encode(key), getValueCodec().encode(value),
				option, time, setOption);
	}

	@Override
	default RedisResponse<Boolean> setbit(K key, long offset, boolean value) {
		return getSourceRedisStringPipelineCommands().setbit(getKeyCodec().encode(key), offset, value);
	}

	@Override
	default RedisResponse<String> setex(K key, long seconds, V value) {
		return getSourceRedisStringPipelineCommands().setex(getKeyCodec().encode(key), seconds,
				getValueCodec().encode(value));
	}

	@Override
	default RedisResponse<Long> setNX(K key, V value) {
		return getSourceRedisStringPipelineCommands().setNX(getKeyCodec().encode(key), getValueCodec().encode(value));
	}

	@Override
	default RedisResponse<Long> setrange(K key, Long offset, V value) {
		return getSourceRedisStringPipelineCommands().setrange(getKeyCodec().encode(key), offset,
				getValueCodec().encode(value));
	}

	@Override
	default RedisResponse<Long> strlen(K key) {
		return getSourceRedisStringPipelineCommands().strlen(getKeyCodec().encode(key));
	}
}
