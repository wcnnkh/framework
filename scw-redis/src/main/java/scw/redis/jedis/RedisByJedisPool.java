package scw.redis.jedis;

import scw.codec.Codec;
import scw.context.annotation.Provider;
import scw.io.Serializer;
import scw.io.SerializerUtils;
import scw.redis.RedisImpl;

@Provider
public final class RedisByJedisPool extends RedisImpl {

	public RedisByJedisPool(JedisResourceFactory jedisResourceFactory) {
		this(jedisResourceFactory, SerializerUtils.DEFAULT_SERIALIZER);
	}

	public RedisByJedisPool(JedisResourceFactory jedisResourceFactory, Serializer serializer) {
		this(jedisResourceFactory, new DefaultJedisStringCodec(), serializer);
	}

	public RedisByJedisPool(JedisResourceFactory jedisResourceFactory, Codec<String, byte[]> codec, Serializer serializer) {
		super(new JedisBinaryOperations(jedisResourceFactory), new JedisStringOperations(jedisResourceFactory),
				codec, serializer);
	}
}
