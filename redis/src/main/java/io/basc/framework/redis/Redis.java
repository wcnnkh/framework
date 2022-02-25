package io.basc.framework.redis;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.basc.framework.codec.support.CharsetCodec;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.DataOperations;
import io.basc.framework.data.geo.Lbs;
import io.basc.framework.io.CrossLanguageSerializer;
import io.basc.framework.io.JavaSerializer;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.lang.Constants;
import io.basc.framework.redis.convert.DefaultConvertibleRedisClient;
import io.basc.framework.util.Assert;
import io.basc.framework.value.AnyValue;

public class Redis extends DefaultConvertibleRedisClient<RedisClient<byte[], byte[]>, byte[], String, byte[], String>
		implements DataOperations {
	private static final String INCR_AND_INIT_SCRIPT = ResourceUtils
			.getContent(ResourceUtils.getSystemResource("/io/basc/framework/redis/incr.script"), Constants.UTF_8);
	private static final String DECR_AND_INIT_SCRIPT = ResourceUtils
			.getContent(ResourceUtils.getSystemResource("/io/basc/framework/redis/decr.script"), Constants.UTF_8);
	private CrossLanguageSerializer serializer = JavaSerializer.INSTANCE;

	public Redis(RedisClient<byte[], byte[]> source) {
		super(source, CharsetCodec.DEFAULT, CharsetCodec.DEFAULT);
	}

	public Lbs<String> getLbs(String key) {
		return new RedisLbs<String, String>(this, key);
	}

	public CrossLanguageSerializer getSerializer() {
		return serializer;
	}

	public void setSerializer(CrossLanguageSerializer serializer) {
		Assert.requiredArgument(serializer != null, "serializer");
		this.serializer = serializer;
	}

	private long toLongValue(Object value) {
		if (value == null) {
			return 0L;
		}

		if (value instanceof byte[]) {
			String v = getValueCodec().decode((byte[]) value);
			return Long.parseLong(v);
		}
		return new AnyValue(value).getAsLongValue();
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
	public boolean touch(String key, long exp, TimeUnit expUnit) {
		Long success = touch(key);
		if (success == null || success == 0) {
			return false;
		}

		Long value = pexpire(key, expUnit.toMillis(exp));
		return value != null && value != 0;
	}
}
