package io.basc.framework.redis.convert;

import io.basc.framework.redis.RedisCodec;
import io.basc.framework.redis.RedisConnectionCommands;

public interface ConvertibleRedisConnectionCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisConnectionCommands<K, V> {

	RedisConnectionCommands<SK, SV> getSourceRedisConnectionCommands();

	@Override
	default V ping(K message) {
		SV value = getSourceRedisConnectionCommands().ping(getKeyCodec().encode(message));
		return getValueCodec().decode(value);
	}

	@Override
	default String select(int index) {
		return getSourceRedisConnectionCommands().select(index);
	}
}
