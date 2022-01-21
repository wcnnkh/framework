package io.basc.framework.redis.convert;

import io.basc.framework.redis.RedisClient;
import io.basc.framework.redis.RedisConnection;

public interface ConvertibleRedisClient<SK, K, SV, V> extends RedisCodec<SK, K, SV, V>, RedisClient<K, V> {

	RedisClient<SK, SV> getSourceRedisClient();

	@Override
	default RedisConnection<K, V> getConnection() {
		RedisConnection<SK, SV> connection = getSourceRedisClient().getConnection();
		return new DefaultConvertibleRedisConnection<>(connection, getKeyCodec(), getValueCodec());
	}
}
