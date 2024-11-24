package io.basc.framework.redis.convert;

import java.util.List;
import java.util.Map;

import io.basc.framework.core.convert.transform.stractegy.CollectionFactory;
import io.basc.framework.redis.BitOP;
import io.basc.framework.redis.ExpireOption;
import io.basc.framework.redis.RedisStringCommands;
import io.basc.framework.redis.SetOption;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisStringCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisStringCommands<K, V> {

	RedisStringCommands<SK, SV> getSourceRedisStringCommands();

	@Override
	default Long append(K key, V value) {
		return getSourceRedisStringCommands().append(getKeyCodec().encode(key), getValueCodec().encode(value));
	}

	@Override
	default Long bitcount(K key, long start, long end) {
		return getSourceRedisStringCommands().bitcount(getKeyCodec().encode(key), start, end);
	}

	@Override
	default Long bitop(BitOP op, K destkey, K... srcKeys) {
		return getSourceRedisStringCommands().bitop(op, getKeyCodec().encode(destkey),
				getKeyCodec().encodeAll(srcKeys));
	}

	@Override
	default Long bitpos(K key, boolean bit, Long start, Long end) {
		return getSourceRedisStringCommands().bitpos(getKeyCodec().encode(key), bit, start, end);
	}

	@Override
	default Long decr(K key) {
		return getSourceRedisStringCommands().decr(getKeyCodec().encode(key));
	}

	@Override
	default Long decrBy(K key, long decrement) {
		return getSourceRedisStringCommands().decrBy(getKeyCodec().encode(key), decrement);
	}

	@Override
	default V get(K key) {
		SV v = getSourceRedisStringCommands().get(getKeyCodec().encode(key));
		return getValueCodec().decode(v);
	}

	@Override
	default Boolean getbit(K key, Long offset) {
		return getSourceRedisStringCommands().getbit(getKeyCodec().encode(key), offset);
	}

	@Override
	default V getdel(K key) {
		SV v = getSourceRedisStringCommands().getdel(getKeyCodec().encode(key));
		return getValueCodec().decode(v);
	}

	@Override
	default V getEx(K key, ExpireOption option, Long time) {
		SV v = getSourceRedisStringCommands().getEx(getKeyCodec().encode(key), option, time);
		return getValueCodec().decode(v);
	}

	@Override
	default V getrange(K key, long startOffset, long endOffset) {
		SV v = getSourceRedisStringCommands().getrange(getKeyCodec().encode(key), startOffset, endOffset);
		return getValueCodec().decode(v);
	}

	@Override
	default V getset(K key, V value) {
		SV v = getSourceRedisStringCommands().getset(getKeyCodec().encode(key), getValueCodec().encode(value));
		return getValueCodec().decode(v);
	}

	@Override
	default Long incr(K key) {
		return getSourceRedisStringCommands().incr(getKeyCodec().encode(key));
	}

	@Override
	default Long incrBy(K key, long increment) {
		return getSourceRedisStringCommands().incrBy(getKeyCodec().encode(key), increment);
	}

	@Override
	default Double incrByFloat(K key, double increment) {
		return getSourceRedisStringCommands().incrByFloat(getKeyCodec().encode(key), increment);
	}

	@Override
	default List<V> mget(K... keys) {
		List<SV> values = getSourceRedisStringCommands().mget(getKeyCodec().encodeAll(keys));
		return getValueCodec().decodeAll(values);
	}

	@Override
	default Boolean mset(Map<K, V> pairs) {
		return getSourceRedisStringCommands().mset(CollectionFactory.convert(pairs, getKeyCodec().toEncodeProcessor(),
				getValueCodec().toEncodeProcessor()));
	}

	@Override
	default Long msetnx(Map<K, V> pairs) {
		return getSourceRedisStringCommands().msetnx(CollectionFactory.convert(pairs, getKeyCodec().toEncodeProcessor(),
				getValueCodec().toEncodeProcessor()));
	}

	@Override
	default Boolean psetex(K key, long milliseconds, V value) {
		return getSourceRedisStringCommands().psetex(getKeyCodec().encode(key), milliseconds,
				getValueCodec().encode(value));
	}

	@Override
	default String set(K key, V value) {
		return getSourceRedisStringCommands().set(getKeyCodec().encode(key), getValueCodec().encode(value));
	}

	@Override
	default Boolean set(K key, V value, ExpireOption option, long time, SetOption setOption) {
		return getSourceRedisStringCommands().set(getKeyCodec().encode(key), getValueCodec().encode(value), option,
				time, setOption);
	}

	@Override
	default Boolean setbit(K key, long offset, boolean value) {
		return getSourceRedisStringCommands().setbit(getKeyCodec().encode(key), offset, value);
	}

	@Override
	default Boolean setex(K key, long seconds, V value) {
		return getSourceRedisStringCommands().setex(getKeyCodec().encode(key), seconds, getValueCodec().encode(value));
	}

	@Override
	default Boolean setNX(K key, V value) {
		return getSourceRedisStringCommands().setNX(getKeyCodec().encode(key), getValueCodec().encode(value));
	}

	@Override
	default Long setrange(K key, Long offset, V value) {
		return getSourceRedisStringCommands().setrange(getKeyCodec().encode(key), offset,
				getValueCodec().encode(value));
	}

	@Override
	default Long strlen(K key) {
		return getSourceRedisStringCommands().strlen(getKeyCodec().encode(key));
	}
}
