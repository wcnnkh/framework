package io.basc.framework.redis.convert;

import java.util.List;

import io.basc.framework.redis.InsertPosition;
import io.basc.framework.redis.MovePosition;
import io.basc.framework.redis.RedisCodec;
import io.basc.framework.redis.RedisListsCommands;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisListsCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisListsCommands<K, V> {

	RedisListsCommands<SK, SV> getSourceRedisListsCommands();

	@Override
	default V blmove(K sourceKey, K destinationKey, MovePosition from, MovePosition to, long timout) {
		SK sk = getKeyCodec().encode(sourceKey);
		SK dk = getKeyCodec().encode(destinationKey);
		SV v = getSourceRedisListsCommands().blmove(sk, dk, from, to, timout);
		return getValueCodec().decode(v);
	}

	@Override
	default List<V> blpop(double timeout, K... keys) {
		SK[] ks = getKeyCodec().encode(keys);
		List<SV> values = getSourceRedisListsCommands().blpop(timeout, ks);
		return getValueCodec().decode(values);
	}

	@Override
	default List<V> brpop(double timeout, K... keys) {
		SK[] ks = getKeyCodec().encode(keys);
		List<SV> values = getSourceRedisListsCommands().brpop(timeout, ks);
		return getValueCodec().decode(values);
	}

	@Override
	default V brpoplpush(K sourceKey, K destinationKey, double timout) {
		SK sk = getKeyCodec().encode(sourceKey);
		SK dk = getKeyCodec().encode(destinationKey);
		SV v = getSourceRedisListsCommands().brpoplpush(sk, dk, timout);
		return getValueCodec().decode(v);
	}

	@Override
	default V lindex(K key, long index) {
		SK k = getKeyCodec().encode(key);
		SV v = getSourceRedisListsCommands().lindex(k, index);
		return getValueCodec().decode(v);
	}

	@Override
	default Long linsert(K key, InsertPosition position, V pivot, V value) {
		SK k = getKeyCodec().encode(key);
		SV pv = getValueCodec().encode(pivot);
		SV tv = getValueCodec().encode(value);
		return getSourceRedisListsCommands().linsert(k, position, pv, tv);
	}

	@Override
	default Long llen(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisListsCommands().llen(k);
	}

	@Override
	default V lmove(K sourceKey, K destinationKey, MovePosition from, MovePosition to) {
		SK sk = getKeyCodec().encode(sourceKey);
		SK dk = getKeyCodec().encode(destinationKey);
		SV v = getSourceRedisListsCommands().lmove(sk, dk, from, to);
		return getValueCodec().decode(v);
	}

	@Override
	default List<V> lpop(K key, int count) {
		SK k = getKeyCodec().encode(key);
		List<SV> values = getSourceRedisListsCommands().lpop(k, count);
		return getValueCodec().decode(values);
	}

	@Override
	default Long lpush(K key, V... elements) {
		SK k = getKeyCodec().encode(key);
		SV[] vs = getValueCodec().encode(elements);
		return getSourceRedisListsCommands().lpush(k, vs);
	}

	@Override
	default Long lpushx(K key, V... elements) {
		SK k = getKeyCodec().encode(key);
		SV[] vs = getValueCodec().encode(elements);
		return getSourceRedisListsCommands().lpushx(k, vs);
	}

	@Override
	default List<V> lrange(K key, long start, long stop) {
		SK k = getKeyCodec().encode(key);
		List<SV> values = getSourceRedisListsCommands().lrange(k, start, stop);
		return getValueCodec().decode(values);
	}

	@Override
	default Long lrem(K key, int count, V element) {
		SK k = getKeyCodec().encode(key);
		SV v = getValueCodec().encode(element);
		return getSourceRedisListsCommands().lrem(k, count, v);
	}

	@Override
	default Boolean lset(K key, long index, V element) {
		SK k = getKeyCodec().encode(key);
		SV v = getValueCodec().encode(element);
		return getSourceRedisListsCommands().lset(k, index, v);
	}

	@Override
	default Boolean ltrim(K key, long start, long stop) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisListsCommands().ltrim(k, start, stop);
	}

	@Override
	default List<V> rpop(K key, int count) {
		SK k = getKeyCodec().encode(key);
		List<SV> values = getSourceRedisListsCommands().rpop(k, count);
		return getValueCodec().decode(values);
	}

	@Override
	default V rpoplpush(K sourceKey, K destinationKey) {
		SK sk = getKeyCodec().encode(sourceKey);
		SK dk = getKeyCodec().encode(destinationKey);
		SV v = getSourceRedisListsCommands().rpoplpush(sk, dk);
		return getValueCodec().decode(v);
	}

	@Override
	default Long rpush(K key, V... elements) {
		SK k = getKeyCodec().encode(key);
		SV[] vs = getValueCodec().encode(elements);
		return getSourceRedisListsCommands().rpush(k, vs);
	}

	@Override
	default Long rpushx(K key, V... elements) {
		SK k = getKeyCodec().encode(key);
		SV[] vs = getValueCodec().encode(elements);
		return getSourceRedisListsCommands().rpushx(k, vs);
	}
}
