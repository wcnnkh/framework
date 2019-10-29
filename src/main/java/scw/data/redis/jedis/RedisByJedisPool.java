package scw.data.redis.jedis;

import scw.core.string.StringCodec;
import scw.data.redis.AbstractRedisWrapper;
import scw.data.redis.Redis;
import scw.data.redis.RedisImpl;
import scw.data.redis.RedisOperations;
import scw.io.SerializerUtils;
import scw.io.serializer.Serializer;

public final class RedisByJedisPool extends AbstractRedisWrapper {
	private final Redis redis;

	public RedisByJedisPool(JedisResourceFactory jedisResourceFactory) {
		this(jedisResourceFactory, SerializerUtils.DEFAULT_SERIALIZER);
	}

	public RedisByJedisPool(JedisResourceFactory jedisResourceFactory, Serializer serializer) {
		this(jedisResourceFactory, new DefaultJedisStringCodec(), serializer);
	}

	public RedisByJedisPool(JedisResourceFactory jedisResourceFactory, StringCodec stringCodec, Serializer serializer) {
		RedisOperations<String, String> stringOperations = new JedisStringOperations(jedisResourceFactory);
		RedisOperations<byte[], byte[]> binaryOperations = new JedisBinaryOperations(jedisResourceFactory);
		this.redis = new RedisImpl(binaryOperations, stringOperations, stringCodec, serializer);
	}

	@Override
	public Redis getTargetRedis() {
		return redis;
	}
}
