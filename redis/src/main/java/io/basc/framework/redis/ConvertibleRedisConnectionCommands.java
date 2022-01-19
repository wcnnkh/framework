package io.basc.framework.redis;

public interface ConvertibleRedisConnectionCommands<SK, K, SV, V> extends RedisCodec<SK, K, SV, V>, RedisConnectionCommands<K, V>{
	
	RedisConnectionCommands<SK, SV> getSourceRedisConnectionCommands();
	
	@Override
	default V ping(K message) {
		SV value = getSourceRedisConnectionCommands().ping(getKeyCodec().decode(message));
		return getValueCodec().encode(value);
	}
	
	@Override
	default String select(int index) {
		return getSourceRedisConnectionCommands().select(index);
	}
}
