package io.basc.framework.redis.cas;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.CAS;
import io.basc.framework.data.TemporaryStorageCasOperations;
import io.basc.framework.io.SerializerUtils;
import io.basc.framework.redis.Redis;
import io.basc.framework.redis.RedisClient;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.value.AnyValue;

public class RedisCASOperations implements TemporaryStorageCasOperations {
	private static final String CAS_IS_NULL = "if (" + isNullScript("cas") + ") then cas = 0 end";

	private static final String CAS_KEY_PREFIX = "cas_";
	private static final String CAS_EXP_SCRIPT = "local cas = redis.call('get', KEYS[2]) " + CAS_IS_NULL + " if ("
			+ notNullScript("(KEYS[3] == cas)")
			+ ") then redis.call('set', KEYS[1], ARGV[1], 'EX', KEYS[4]) if redis.call('exists', KEYS[2]) == 1 then redis.call('incr', KEYS[2]) else redis.call('set', KEYS[2], 1, 'EX', KEYS[4]) end return 1 else return 0 end";
	private static final String CAS_SCRIPT = "local cas = redis.call('get', KEYS[2]) " + CAS_IS_NULL + " if ("
			+ notNullScript("(KEYS[3] == cas)")
			+ ") then redis.call('set', KEYS[1], ARGV[1]) redis.call('incr', KEYS[2]) return 1 else return 0 end";
	private static final String CAS_DELETE = "local cas = redis.call('get', KEYS[2]) " + CAS_IS_NULL + " if ("
			+ notNullScript("(ARGV[2] == cas)")
			+ ") then redis.call('del', KEYS[2]) return redis.call('del', KEYS[1]) else return 0 end";
	private static final String CAS_GET = "if redis.call('exists', KEYS[1]) == 1 then local cas = redis.call('get', KEYS[2]) "
			+ CAS_IS_NULL
			+ " local res = {} res[1] = redis.call('get', KEYS[1]) res[2] = cas return res else return nil end";
	private static final String ADD_EXP = "if redis.call('exists', KEYS[1]) == 1 then return 0 else redis.call('set', KEYS[1], ARGV[1], 'EX', KEYS[3]) if redis.call('exists', KEYS[2]) == 1 then redis.call('incr', KEYS[2]) else redis.call('set', KEYS[2], 1, 'EX', KEYS[3]) end return 0  end";
	private static final String ADD = "if redis.call('exists', KEYS[1]) == 1 then return 0 else redis.call('set', KEYS[1], ARGV[1]) redis.call('incr', KEYS[2], 1) return 0  end";
	private static final String SET_EXP = "redis.call('set', KEYS[1], ARGV[1], 'EX', KEYS[3]) if redis.call('exists', KEYS[2]) == 1 then redis.call('incr', KEYS[2]) else redis.call('set', KEYS[2], 1, 'EX', KEYS[3]) end";
	private static final String SET = "redis.call('set', KEYS[1], ARGV[1]) redis.call('incr', KEYS[2], 1) end";
	private static final String setIfPresent_EXP = "if redis.call('exists', KEYS[1]) == 0 then return 0 else redis.call('set', KEYS[1], ARGV[1], 'EX', KEYS[3]) if redis.call('exists', KEYS[2]) == 1 then redis.call('incr', KEYS[2]) else redis.call('set', KEYS[2], 1, 'EX', KEYS[3]) end return 0  end";
	private static final String setIfPresent = "if redis.call('exists', KEYS[1]) == 0 then return 0 else redis.call('set', KEYS[1], ARGV[1]) redis.call('incr', KEYS[2], 1) return 0  end";

	private static String notNullScript(String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append(" ~= nil");
		sb.append(" or (type(").append(name).append(") == 'boolean' and ").append(name).append(")");
		return sb.toString();
	}

	private static String isNullScript(String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append(" == nil");
		sb.append(" or (type(").append(name).append(") == 'boolean' and ").append(name).append(" == false)");
		return sb.toString();
	}

	private RedisClient<String, Object> client;

	public RedisCASOperations(Redis redis) {
		this(redis.to(SerializerUtils.getSerializer()));
	}

	public RedisCASOperations(RedisClient<String, Object> client) {
		this.client = client;
	}

	@Override
	public boolean cas(String key, Object value, TypeDescriptor valueType, long cas, long exp, TimeUnit expUnit) {
		Object resposne;
		if (exp > 0) {
			resposne = client.eval(CAS_EXP_SCRIPT, Arrays.asList(key, CAS_KEY_PREFIX + key, cas + "", exp + ""),
					Arrays.asList(value));
		} else {
			resposne = client.eval(CAS_SCRIPT, Arrays.asList(key, CAS_KEY_PREFIX + key, cas + ""),
					Arrays.asList(value));
		}
		return new AnyValue(resposne).getAsBooleanValue();
	}

	public boolean delete(String key, long cas) {
		Object v = client.eval(CAS_DELETE, Arrays.asList(key, CAS_KEY_PREFIX + key, cas + ""), null);
		return new AnyValue(v).getAsBooleanValue();
	}

	public CAS<Object> gets(String key) {
		List<Object> values = client.eval(CAS_GET, Arrays.asList(key, CAS_KEY_PREFIX + key), null);
		if (CollectionUtils.isEmpty(values) || values.size() != 2) {
			return null;
		}

		Object value = values.get(0);
		long verion = new AnyValue(values.get(1)).getAsLongValue();
		return new CAS<>(verion, value);
	}

	public boolean delete(String key) {
		Long value = client.del(key, CAS_KEY_PREFIX + key);
		return value != null && value >= 1;
	}

	@Override
	public Object get(String key) {
		return client.get(key);
	}

	@Override
	public boolean exists(String key) {
		Long value = client.exists(key);
		return value != null && value == 1;
	}

	@Override
	public boolean touch(String key, long exp, TimeUnit expUnit) {
		client.touch(key, CAS_KEY_PREFIX + key);
		return expire(key, exp, expUnit);
	}

	@Override
	public boolean expire(String key, long exp, TimeUnit expUnit) {
		Long value = client.expire(Arrays.asList(key, CAS_KEY_PREFIX + key), exp, expUnit);
		return value != null && value >= 1;
	}

	@Override
	public boolean setIfAbsent(String key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		Object v;
		if (exp > 0) {
			v = client.eval(ADD_EXP, Arrays.asList(key, CAS_KEY_PREFIX + key, expUnit.toMillis(exp) + ""),
					Arrays.asList(value));
		} else {
			v = client.eval(ADD, Arrays.asList(key, CAS_KEY_PREFIX + key), Arrays.asList(value));
		}
		return new AnyValue(v).getAsBooleanValue();
	}

	@Override
	public boolean setIfPresent(String key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		Object v;
		if (exp > 0) {
			v = client.eval(setIfPresent_EXP, Arrays.asList(key, CAS_KEY_PREFIX + key, expUnit.toMillis(exp) + ""),
					Arrays.asList(value));
		} else {
			v = client.eval(setIfPresent, Arrays.asList(key, CAS_KEY_PREFIX + key), Arrays.asList(value));
		}
		return new AnyValue(v).getAsBooleanValue();
	}

	@Override
	public void set(String key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		if (exp > 0) {
			client.eval(SET_EXP, Arrays.asList(key, CAS_KEY_PREFIX + key, expUnit.toMillis(exp) + ""),
					Arrays.asList(value));
		} else {
			client.eval(SET, Arrays.asList(key, CAS_KEY_PREFIX + key), Arrays.asList(value));
		}
	}

	@Override
	public boolean setIfPresent(String key, Object value, TypeDescriptor valueType) {
		Object v = client.eval(setIfPresent, Arrays.asList(key, CAS_KEY_PREFIX + key), Arrays.asList(value));
		return new AnyValue(v).getAsBooleanValue();
	}

	@Override
	public void set(String key, Object value, TypeDescriptor valueType) {
		client.eval(SET, Arrays.asList(key, CAS_KEY_PREFIX + key), Arrays.asList(value));
	}

	@Override
	public boolean setIfAbsent(String key, Object value, TypeDescriptor valueType) {
		Object v = client.eval(ADD, Arrays.asList(key, CAS_KEY_PREFIX + key), Arrays.asList(value));
		return new AnyValue(v).getAsBooleanValue();
	}

	@Override
	public boolean cas(String key, Object value, TypeDescriptor valueType, long cas) {
		Object resposne = client.eval(CAS_SCRIPT, Arrays.asList(key, CAS_KEY_PREFIX + key, cas + ""),
				Arrays.asList(value));
		return new AnyValue(resposne).getAsBooleanValue();
	}
}
