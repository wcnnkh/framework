package io.basc.framework.redis;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.ValueWrapper;
import io.basc.framework.data.DataStorage;
import io.basc.framework.data.TemporaryCounter;
import io.basc.framework.data.TemporaryDataOperations;
import io.basc.framework.data.geo.Lbs;
import io.basc.framework.lang.Constants;
import io.basc.framework.redis.convert.DefaultConvertibleRedisClient;
import io.basc.framework.util.Assert;
import io.basc.framework.util.codec.Codec;
import io.basc.framework.util.codec.support.CharsetCodec;
import io.basc.framework.util.io.ResourceUtils;
import io.basc.framework.util.io.SerializerUtils;
import io.basc.framework.util.io.serializer.CrossLanguageSerializer;

public final class Redis
		extends DefaultConvertibleRedisClient<RedisClient<byte[], byte[]>, byte[], String, byte[], String, Redis>
		implements TemporaryDataOperations, DataStorage, TemporaryCounter {
	private static final String INCR_AND_INIT_SCRIPT = ResourceUtils
			.getContent(ResourceUtils.getSystemResource("/io/basc/framework/redis/incr.script"), Constants.UTF_8);
	private static final String DECR_AND_INIT_SCRIPT = ResourceUtils
			.getContent(ResourceUtils.getSystemResource("/io/basc/framework/redis/decr.script"), Constants.UTF_8);
	private CrossLanguageSerializer serializer = SerializerUtils.getCrossLanguageSerializer();

	public Redis(RedisClient<byte[], byte[]> source) {
		super(source, CharsetCodec.DEFAULT, CharsetCodec.DEFAULT);
	}

	public Lbs<String> getLbs(String key) {
		return new RedisLbs<String, String>(this, key);
	}

	public CrossLanguageSerializer getSerializer() {
		return serializer;
	}

	public RedisClient<String, Object> to(Codec<Object, byte[]> codec) {
		return getSourceRedisClient().to(getKeyCodec(), codec);
	}

	public Redis setSerializer(CrossLanguageSerializer serializer) {
		Assert.requiredArgument(serializer != null, "serializer");
		Redis redis = clone();
		redis.serializer = serializer;
		return redis;
	}

	private long toLongValue(Object value) {
		if (value == null) {
			return 0L;
		}

		if (value instanceof byte[]) {
			String v = getValueCodec().decode((byte[]) value);
			return Long.parseLong(v);
		}
		return ValueWrapper.of(value).getAsLong();
	}

	@Override
	public long incr(String key, long delta, long initialValue, long exp, TimeUnit expUnit) {
		Object value = eval(INCR_AND_INIT_SCRIPT, Arrays.asList(key), Arrays.asList(String.valueOf(delta),
				String.valueOf(initialValue), String.valueOf(expUnit.toMillis(exp))));
		return toLongValue(value);
	}

	@Override
	public long decr(String key, long delta, long initialValue, long exp, TimeUnit expUnit) {
		Object value = eval(DECR_AND_INIT_SCRIPT, Arrays.asList(key), Arrays.asList(String.valueOf(delta),
				String.valueOf(initialValue), String.valueOf(expUnit.toMillis(exp))));
		return toLongValue(value);
	}

	@Override
	public boolean delete(String key) {
		return del(key) == 1;
	}

	@Override
	public boolean exists(String key) {
		return exists(new String[] { key }) == 1;
	}

	@Override
	public <T> T get(TypeDescriptor type, String key) {
		byte[] value = getSourceRedisClient().get(getKeyCodec().encode(key));
		return serializer.deserialize(value, type);
	}

	@Override
	public String get(String key) {
		return super.get(key);
	}

	@Override
	public void set(String key, Object value, TypeDescriptor valueType) {
		byte[] target = serializer.serialize(value, valueType);
		getSourceRedisClient().set(getKeyCodec().encode(key), target);
	}

	@Override
	public boolean setIfAbsent(String key, Object value, TypeDescriptor valueType) {
		byte[] target = serializer.serialize(value, valueType);
		Boolean success = getSourceRedisClient().set(getKeyCodec().encode(key), target, null, 0, SetOption.NX);
		return success == null ? false : success;
	}

	@Override
	public boolean setIfPresent(String key, Object value, TypeDescriptor valueType) {
		byte[] target = serializer.serialize(value, valueType);
		Boolean success = getSourceRedisClient().set(getKeyCodec().encode(key), target, null, 0, SetOption.XX);
		return success == null ? false : success;
	}

	@Override
	public void set(String key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		byte[] target = serializer.serialize(value, valueType);
		getSourceRedisClient().set(getKeyCodec().encode(key), target, ExpireOption.PX, expUnit.toMillis(exp), null);
	}

	@Override
	public boolean setIfAbsent(String key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		byte[] target = serializer.serialize(value, valueType);
		Boolean success = getSourceRedisClient().set(getKeyCodec().encode(key), target, ExpireOption.PX,
				expUnit.toMillis(exp), SetOption.NX);
		return success == null ? false : success;
	}

	@Override
	public boolean setIfPresent(String key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		byte[] target = serializer.serialize(value, valueType);
		Boolean success = getSourceRedisClient().set(getKeyCodec().encode(key), target, ExpireOption.PX,
				expUnit.toMillis(exp), SetOption.XX);
		return success == null ? false : success;
	}

	@Override
	public boolean touch(String key) {
		Long value = touch(new String[] { key });
		return value != null && value != 0;
	}

	@Override
	public boolean touch(String key, long exp, TimeUnit expUnit) {
		if (!touch(key)) {
			return false;
		}

		Long value = pexpire(key, expUnit.toMillis(exp));
		return value != null && value != 0;
	}

	@Override
	public boolean expire(String key, long exp, TimeUnit expUnit) {
		Long value = pexpire(key, expUnit.toMillis(exp));
		return value != null && value == 1;
	}

	@Override
	public long decr(String key, long delta, long initialValue) {
		return decr(key, delta, initialValue, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	public long incr(String key, long delta, long initialValue) {
		return incr(key, delta, initialValue, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	public Long getRemainingSurvivalTime(String key) {
		return pttl(key);
	}
}
