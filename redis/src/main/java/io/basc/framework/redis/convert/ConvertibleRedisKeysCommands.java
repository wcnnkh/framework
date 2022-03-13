package io.basc.framework.redis.convert;

import java.util.LinkedHashSet;
import java.util.Set;

import io.basc.framework.redis.DataType;
import io.basc.framework.redis.MigrateParams;
import io.basc.framework.redis.RedisKeysCommands;
import io.basc.framework.redis.RedisValueEncoding;
import io.basc.framework.redis.RestoreParams;
import io.basc.framework.redis.ScanOptions;
import io.basc.framework.util.page.Pageable;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisKeysCommands<SK, K, SV, V> extends RedisCodec<SK, K, SV, V>, RedisKeysCommands<K, V> {

	RedisKeysCommands<SK, SV> getSourceRedisKeysCommands();

	@Override
	default Boolean copy(K source, K destination, Integer destinationDB, boolean replace) {
		SK sk = getKeyCodec().encode(source);
		SK dk = getKeyCodec().encode(destination);
		return getSourceRedisKeysCommands().copy(sk, dk, destinationDB, replace);
	}

	@Override
	default Long del(K... keys) {
		SK[] ks = getKeyCodec().encodeAll(keys);
		return getSourceRedisKeysCommands().del(ks);
	}

	@Override
	default V dump(K key) {
		SK k = getKeyCodec().encode(key);
		SV value = getSourceRedisKeysCommands().dump(k);
		return getValueCodec().decode(value);
	}

	@Override
	default Long exists(K... keys) {
		SK[] ks = getKeyCodec().encodeAll(keys);
		return getSourceRedisKeysCommands().exists(ks);
	}

	@Override
	default Long expire(K key, long seconds) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysCommands().expire(k, seconds);
	}

	@Override
	default Long expireAt(K key, long timestamp) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysCommands().expireAt(k, timestamp);
	}

	@Override
	default Set<K> keys(K pattern) {
		SK k = getKeyCodec().encode(pattern);
		Set<SK> tvs = getSourceRedisKeysCommands().keys(k);
		return getKeyCodec().toDecodeConverter().convertTo(tvs, new LinkedHashSet<K>(tvs.size()));
	}

	@Override
	default String migrate(String host, int port, K key, int targetDB, int timeout) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysCommands().migrate(host, port, k, targetDB, timeout);
	}

	@Override
	default String migrate(String host, int port, int targetDB, int timeout, MigrateParams option, K... keys) {
		SK[] ks = getKeyCodec().encodeAll(keys);
		return getSourceRedisKeysCommands().migrate(host, port, targetDB, timeout, option, ks);
	}

	@Override
	default Long move(K key, int targetDB) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysCommands().move(k, targetDB);
	}

	@Override
	default Long objectRefCount(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysCommands().objectRefCount(k);
	}

	@Override
	default RedisValueEncoding objectEncoding(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysCommands().objectEncoding(k);
	}

	@Override
	default Long objectIdletime(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysCommands().objectIdletime(k);
	}

	@Override
	default Long objectFreq(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysCommands().objectFreq(k);
	}

	@Override
	default Long persist(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysCommands().persist(k);
	}

	@Override
	default Long pexpire(K key, long milliseconds) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysCommands().pexpire(k, milliseconds);
	}

	@Override
	default Long pexpireAt(K key, long timestamp) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysCommands().pexpireAt(k, timestamp);
	}

	@Override
	default Long pttl(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysCommands().pttl(k);
	}

	@Override
	default K randomkey() {
		SK k = getSourceRedisKeysCommands().randomkey();
		return getKeyCodec().decode(k);
	}

	@Override
	default String rename(K key, K newKey) {
		SK k = getKeyCodec().encode(key);
		SK tkNewKey = getKeyCodec().encode(newKey);
		return getSourceRedisKeysCommands().rename(k, tkNewKey);
	}

	@Override
	default Boolean renamenx(K key, K newKey) {
		SK k = getKeyCodec().encode(key);
		SK tkNewKey = getKeyCodec().encode(newKey);
		return getSourceRedisKeysCommands().renamenx(k, tkNewKey);
	}

	@Override
	default String restore(K key, long ttl, byte[] serializedValue, RestoreParams params) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysCommands().restore(k, ttl, serializedValue, params);
	}

	@Override
	default Pageable<Long, K> scan(long cursorId, ScanOptions<K> options) {
		ScanOptions<SK> to = options.convert(getKeyCodec().toEncodeConverter());
		return getSourceRedisKeysCommands().scan(cursorId, to).map((v) -> getKeyCodec().decode(v));
	}

	@Override
	default Long touch(K... keys) {
		SK[] ks = getKeyCodec().encodeAll(keys);
		return getSourceRedisKeysCommands().touch(ks);
	}

	@Override
	default Long ttl(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysCommands().ttl(k);
	}

	@Override
	default DataType type(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysCommands().type(k);
	}

	@Override
	default Long unlink(K... keys) {
		SK[] ks = getKeyCodec().encodeAll(keys);
		return getSourceRedisKeysCommands().unlink(ks);
	}

	@Override
	default Long wait(int numreplicas, long timeout) {
		return getSourceRedisKeysCommands().wait(numreplicas, timeout);
	}
}
