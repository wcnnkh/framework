package io.basc.framework.redis.convert;

import java.util.LinkedHashSet;
import java.util.Set;

import io.basc.framework.redis.DataType;
import io.basc.framework.redis.MigrateParams;
import io.basc.framework.redis.RedisCodec;
import io.basc.framework.redis.RedisKeysPipelineCommands;
import io.basc.framework.redis.RedisResponse;
import io.basc.framework.redis.RedisValueEncoding;
import io.basc.framework.redis.RestoreParams;
import io.basc.framework.redis.ScanOptions;
import io.basc.framework.util.page.Pageable;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisKeysPipelineCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisKeysPipelineCommands<K, V> {

	RedisKeysPipelineCommands<SK, SV> getSourceRedisKeysPipelineCommands();

	@Override
	default RedisResponse<Boolean> copy(K source, K destination, boolean replace) {
		SK sk = getKeyCodec().encode(source);
		SK dk = getKeyCodec().encode(destination);
		return getSourceRedisKeysPipelineCommands().copy(sk, dk, replace);
	}

	@Override
	default RedisResponse<Long> del(K... keys) {
		SK[] ks = getKeyCodec().encode(keys);
		return getSourceRedisKeysPipelineCommands().del(ks);
	}

	@Override
	default RedisResponse<V> dump(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysPipelineCommands().dump(k).map((value) -> getValueCodec().decode(value));
	}

	@Override
	default RedisResponse<Long> exists(K... keys) {
		SK[] ks = getKeyCodec().encode(keys);
		return getSourceRedisKeysPipelineCommands().exists(ks);
	}

	@Override
	default RedisResponse<Long> expire(K key, long seconds) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysPipelineCommands().expire(k, seconds);
	}

	@Override
	default RedisResponse<Long> expireAt(K key, long timestamp) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysPipelineCommands().expireAt(k, timestamp);
	}

	@Override
	default RedisResponse<Set<K>> keys(K pattern) {
		SK k = getKeyCodec().encode(pattern);
		return getSourceRedisKeysPipelineCommands().keys(k)
				.map((tvs) -> getKeyCodec().toDecodeConverter().convert(tvs, new LinkedHashSet<K>(tvs.size())));
	}

	@Override
	default RedisResponse<String> migrate(String host, int port, K key, int timeout) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysPipelineCommands().migrate(host, port, k, timeout);
	}

	@Override
	default RedisResponse<String> migrate(String host, int port, int timeout, MigrateParams option, K... keys) {
		SK[] ks = getKeyCodec().encode(keys);
		return getSourceRedisKeysPipelineCommands().migrate(host, port, timeout, option, ks);
	}

	@Override
	default RedisResponse<Long> objectRefCount(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysPipelineCommands().objectRefCount(k);
	}

	@Override
	default RedisResponse<RedisValueEncoding> objectEncoding(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysPipelineCommands().objectEncoding(k);
	}

	@Override
	default RedisResponse<Long> objectIdletime(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysPipelineCommands().objectIdletime(k);
	}

	@Override
	default RedisResponse<Long> objectFreq(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysPipelineCommands().objectFreq(k);
	}

	@Override
	default RedisResponse<Long> persist(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysPipelineCommands().persist(k);
	}

	@Override
	default RedisResponse<Long> pexpire(K key, long milliseconds) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysPipelineCommands().pexpire(k, milliseconds);
	}

	@Override
	default RedisResponse<Long> pexpireAt(K key, long timestamp) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysPipelineCommands().pexpireAt(k, timestamp);
	}

	@Override
	default RedisResponse<Long> pttl(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysPipelineCommands().pttl(k);
	}

	@Override
	default RedisResponse<K> randomkey() {
		return getSourceRedisKeysPipelineCommands().randomkey().map((k) -> getKeyCodec().decode(k));
	}

	@Override
	default RedisResponse<String> rename(K key, K newKey) {
		SK k = getKeyCodec().encode(key);
		SK tkNewKey = getKeyCodec().encode(newKey);
		return getSourceRedisKeysPipelineCommands().rename(k, tkNewKey);
	}

	@Override
	default RedisResponse<Long> renamenx(K key, K newKey) {
		SK k = getKeyCodec().encode(key);
		SK tkNewKey = getKeyCodec().encode(newKey);
		return getSourceRedisKeysPipelineCommands().renamenx(k, tkNewKey);
	}

	@Override
	default RedisResponse<String> restore(K key, long ttl, byte[] serializedValue, RestoreParams params) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysPipelineCommands().restore(k, ttl, serializedValue, params);
	}

	@Override
	default RedisResponse<Pageable<Long, K>> scan(long cursorId, ScanOptions<K> options) {
		ScanOptions<SK> to = options.convert(getKeyCodec().toEncodeConverter());
		return getSourceRedisKeysPipelineCommands().scan(cursorId, to)
				.map((p) -> p.map((v) -> getKeyCodec().decode(v)));
	}

	@Override
	default RedisResponse<Long> touch(K... keys) {
		SK[] ks = getKeyCodec().encode(keys);
		return getSourceRedisKeysPipelineCommands().touch(ks);
	}

	@Override
	default RedisResponse<Long> ttl(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysPipelineCommands().ttl(k);
	}

	@Override
	default RedisResponse<DataType> type(K key) {
		SK k = getKeyCodec().encode(key);
		return getSourceRedisKeysPipelineCommands().type(k);
	}

	@Override
	default RedisResponse<Long> unlink(K... keys) {
		SK[] ks = getKeyCodec().encode(keys);
		return getSourceRedisKeysPipelineCommands().unlink(ks);
	}
}
