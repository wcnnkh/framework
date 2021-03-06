package scw.redis.jedis;

import scw.context.annotation.Provider;
import scw.io.Serializer;
import scw.io.SerializerUtils;
import scw.redis.RedisImpl;
import scw.util.StringCodec;

@Provider
public final class RedisByJedisPool extends RedisImpl {

	public RedisByJedisPool(JedisResourceFactory jedisResourceFactory) {
		this(jedisResourceFactory, SerializerUtils.DEFAULT_SERIALIZER);
	}

	public RedisByJedisPool(JedisResourceFactory jedisResourceFactory, Serializer serializer) {
		this(jedisResourceFactory, new DefaultJedisStringCodec(), serializer);
	}

	public RedisByJedisPool(JedisResourceFactory jedisResourceFactory, StringCodec stringCodec, Serializer serializer) {
		super(new JedisBinaryOperations(jedisResourceFactory), new JedisStringOperations(jedisResourceFactory),
				stringCodec, serializer);
	}
}
