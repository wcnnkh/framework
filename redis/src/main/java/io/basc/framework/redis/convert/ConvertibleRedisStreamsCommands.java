package io.basc.framework.redis.convert;

import java.util.List;

import io.basc.framework.redis.ClaimArgs;
import io.basc.framework.redis.RedisCodec;
import io.basc.framework.redis.RedisStreamsCommands;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisStreamsCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisStreamsCommands<K, V> {
	
	RedisStreamsCommands<SK, SV> getSourceRedisStreamsCommands();

	@Override
	default Long xack(K key, K group, K... ids) {
		return getSourceRedisStreamsCommands().xack(getKeyCodec().encode(key), getKeyCodec().encode(group),
				getKeyCodec().encode(ids));
	}

	@Override
	default List<V> xclaim(K key, K group, K consumer, long minIdleTime, ClaimArgs args, K... ids) {
		List<SV> values = getSourceRedisStreamsCommands().xclaim(getKeyCodec().encode(key), getKeyCodec().encode(group),
				getKeyCodec().encode(consumer), minIdleTime, args, getKeyCodec().encode(ids));
		return getValueCodec().decode(values);
	}

	@Override
	default Long xdel(K key, K... ids) {
		return getSourceRedisStreamsCommands().xdel(getKeyCodec().encode(key), getKeyCodec().encode(ids));
	}
}
