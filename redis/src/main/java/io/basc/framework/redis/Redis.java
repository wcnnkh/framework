package io.basc.framework.redis;

import java.util.Arrays;

import io.basc.framework.codec.support.CharsetCodec;
import io.basc.framework.data.DataOperations;
import io.basc.framework.data.cas.CASOperations;
import io.basc.framework.data.geo.Lbs;
import io.basc.framework.io.JavaSerializer;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.io.Serializer;
import io.basc.framework.lang.Constants;
import io.basc.framework.redis.cas.RedisCASOperations;
import io.basc.framework.redis.convert.DefaultConvertibleRedisClient;
import io.basc.framework.value.AnyValue;

public class Redis extends DefaultConvertibleRedisClient<RedisClient<byte[], byte[]>, byte[], String, byte[], String> {
	private static final String INCR_AND_INIT_SCRIPT = ResourceUtils
			.getContent(ResourceUtils.getSystemResource("/io/basc/framework/redis/incr.script"), Constants.UTF_8);
	private static final String DECR_AND_INIT_SCRIPT = ResourceUtils
			.getContent(ResourceUtils.getSystemResource("/io/basc/framework/redis/decr.script"), Constants.UTF_8);

	private Serializer serializer = JavaSerializer.INSTANCE;
	private final RedisDataOperations dataOperations = new RedisDataOperations(this);

	public Redis(RedisClient<byte[], byte[]> source) {
		super(source, CharsetCodec.DEFAULT, CharsetCodec.DEFAULT);
	}

	public RedisClient<String, Object> getRedisObjectClient() {
		return new DefaultConvertibleRedisClient<>(client, getKeyCodec(), serializer.toCodec());
	}

	public CASOperations getCASOperations() {
		return new RedisCASOperations(getRedisObjectClient());
	}

	public DataOperations getDataOperations() {
		return dataOperations;
	}

	public Lbs<String> getLbs(String key) {
		return new RedisLbs<String, String>(this, key);
	}

	public Serializer getSerializer() {
		return serializer;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	public Long incr(String key, long delta, long initialValue, long exp) {
		Object value = eval(INCR_AND_INIT_SCRIPT, Arrays.asList(key),
				Arrays.asList(String.valueOf(delta), String.valueOf(initialValue), String.valueOf(exp)));
		return new AnyValue(value).getAsLong();
	}

	public Long decr(String key, long delta, long initialValue, long exp) {
		Object value = eval(DECR_AND_INIT_SCRIPT, Arrays.asList(key),
				Arrays.asList(String.valueOf(delta), String.valueOf(initialValue), String.valueOf(exp)));
		return new AnyValue(value).getAsLong();
	}

}
