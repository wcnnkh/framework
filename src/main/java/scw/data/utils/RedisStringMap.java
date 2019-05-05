package scw.data.utils;

import java.nio.charset.Charset;

import scw.core.Constants;
import scw.data.redis.Redis;
import scw.data.redis.serialize.StringRedisSerialize;

public class RedisStringMap extends RedisMap<String> {

	public RedisStringMap(Redis redis, String key) {
		this(redis, key, Constants.DEFAULT_CHARSET);
	}

	public RedisStringMap(Redis redis, String key, Charset charset) {
		super(redis.getBinaryOperations(), key, charset,
				new StringRedisSerialize(charset));
	}

}
