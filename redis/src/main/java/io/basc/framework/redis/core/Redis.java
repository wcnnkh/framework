package io.basc.framework.redis.core;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.support.CharsetCodec;
import io.basc.framework.data.DataOperations;
import io.basc.framework.data.cas.CASOperations;
import io.basc.framework.data.geo.Lbs;
import io.basc.framework.io.JavaSerializer;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.io.Serializer;
import io.basc.framework.lang.Constants;
import io.basc.framework.redis.core.cas.RedisCASOperations;
import io.basc.framework.redis.core.convert.ConvertibleRedisConnection;
import io.basc.framework.redis.core.convert.ConvertibleRedisConnectionFactory;
import io.basc.framework.value.AnyValue;

import java.util.Arrays;

public class Redis implements RedisConnectionFactory<String, String> {
	private static final String INCR_AND_INIT_SCRIPT = ResourceUtils
			.getContent(ResourceUtils.getSystemResource("/io/basc/framework/redis/core/incr.script"), Constants.UTF_8);
	private static final String DECR_AND_INIT_SCRIPT = ResourceUtils
			.getContent(ResourceUtils.getSystemResource("/io/basc/framework/redis/core/decr.script"), Constants.UTF_8);

	private Codec<String, byte[]> keyCodec = CharsetCodec.DEFAULT;
	private Codec<String, byte[]> valueCodec = CharsetCodec.DEFAULT;
	private Serializer serializer = JavaSerializer.INSTANCE;
	private final RedisDataOperations dataOperations = new RedisDataOperations(this);

	private final RedisConnectionFactory<byte[], byte[]> targetConnectionFactory;

	public Redis(RedisConnectionFactory<byte[], byte[]> targetConnectionFactory) {
		this.targetConnectionFactory = targetConnectionFactory;
	}

	@Override
	public RedisConnection<String, String> getConnection() {
		RedisConnection<byte[], byte[]> connection = targetConnectionFactory.getConnection();
		return new ConvertibleRedisConnection<byte[], byte[], String, String>(connection, keyCodec, valueCodec);
	}

	public RedisCommands<String, Object> getObjectCommands() {
		return new ConvertibleRedisConnectionFactory<byte[], byte[], String, Object>(targetConnectionFactory, keyCodec,
				serializer.toCodec());
	}

	public RedisCommands<byte[], byte[]> getBinaryCommands() {
		return targetConnectionFactory;
	}

	public CASOperations getCASOperations() {
		return new RedisCASOperations(getObjectCommands());
	}

	public DataOperations getDataOperations() {
		return dataOperations;
	}

	public Lbs<String> getMarkerManager(String key) {
		return new RedisLbs<String, String>(this, key);
	}

	public Serializer getSerializer() {
		return serializer;
	}

	public void setSerializer(Serializer serializer) {
		this.serializer = serializer;
	}

	public final Codec<String, byte[]> getKeyCodec() {
		return keyCodec;
	}

	public void setKeyCodec(Codec<String, byte[]> keyCodec) {
		this.keyCodec = keyCodec;
	}

	public final Codec<String, byte[]> getValueCodec() {
		return valueCodec;
	}

	public void setValueCodec(Codec<String, byte[]> valueCodec) {
		this.valueCodec = valueCodec;
	}

	public Long incr(String key, long delta, long initialValue, int exp) {
		Object value = eval(INCR_AND_INIT_SCRIPT, Arrays.asList(key),
				Arrays.asList(String.valueOf(delta), String.valueOf(initialValue), String.valueOf(exp)));
		return new AnyValue(value).getAsLong();
	}

	public Long decr(String key, long delta, long initialValue, int exp) {
		Object value = eval(DECR_AND_INIT_SCRIPT, Arrays.asList(key),
				Arrays.asList(String.valueOf(delta), String.valueOf(initialValue), String.valueOf(exp)));
		return new AnyValue(value).getAsLong();
	}

}
