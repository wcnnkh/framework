package scw.data.redis.jedis;

import scw.core.instance.annotation.Configuration;
import scw.data.redis.RedisImpl;
import scw.io.serialzer.Serializer;
import scw.io.serialzer.SerializerUtils;
import scw.util.StringCodec;

@Configuration(order = Integer.MIN_VALUE + 200)
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
