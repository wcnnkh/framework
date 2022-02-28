package io.basc.framework.redis.cas;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.CAS;
import io.basc.framework.data.TemporaryStorageCasOperations;
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
			+ ") then redis.call('del', KEYS[1]) redis.call('del', KEYS[2])  return 1 else return 0 end";
	private static final String DELETE = "redis.call('del', KEYS[1]) redis.call('del', KEYS[2])";
	private static final String CAS_GET = "if redis.call('exists', KEYS[1]) == 1 then local cas = redis.call('get', KEYS[2]) "
			+ CAS_IS_NULL
			+ " local res = {} res[1] = redis.call('get', KEYS[1]) res[2] = cas return res else return nil end";
	private static final String ADD_EXP = "if redis.call('exists', KEYS[1]) == 1 then return 0 else redis.call('set', KEYS[1], ARGV[1], 'EX', KEYS[3]) if redis.call('exists', KEYS[2]) == 1 then redis.call('incr', KEYS[2]) else redis.call('set', KEYS[2], 1, 'EX', KEYS[3]) end return 0  end";
	private static final String ADD = "if redis.call('exists', KEYS[1]) == 1 then return 0 else redis.call('set', KEYS[1], ARGV[1]) redis.call('incr', KEYS[2], 1) return 0  end";
	private static final String SET_EXP = "redis.call('set', KEYS[1], ARGV[1], 'EX', KEYS[3]) if redis.call('exists', KEYS[2]) == 1 then redis.call('incr', KEYS[2]) else redis.call('set', KEYS[2], 1, 'EX', KEYS[3]) end";
	private static final String SET = "redis.call('set', KEYS[1], ARGV[1]) redis.call('incr', KEYS[2], 1) end";

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

	private Redis redis;

	public RedisCASOperations(Redis redis) {
		this.redis = redis;
	}
	
	@Override
	public boolean cas(String key, Object value, TypeDescriptor valueType, long cas, long exp, TimeUnit expUnit) {
		byte[] target = redis.getSerializer().serialize(value, valueType);
		Object resposne;
		if (exp > 0) {
			resposne = redis.getSourceRedisClient().eval(redis.getKeyCodec().encode(CAS_EXP_SCRIPT), redis.getKeyCodec().encode(Arrays.asList(key, CAS_KEY_PREFIX + key, cas + "", exp + "")),
					Arrays.asList(target));
		} else {
			resposne = redis.getSourceRedisClient().eval(redis.getKeyCodec().encode(CAS_SCRIPT), redis.getKeyCodec().encode(Arrays.asList(key, CAS_KEY_PREFIX + key, cas + "")),
					Arrays.asList(target));
		}
		return new AnyValue(resposne).getAsBooleanValue();
	}

	public boolean delete(String key, long cas) {
		Object v = redis.eval(CAS_DELETE, Arrays.asList(key, CAS_KEY_PREFIX + key, cas + ""), null);
		return new AnyValue(v).getAsBooleanValue();
	}

	@SuppressWarnings("unchecked")
	public CAS<Object> gets(String key) {
		List<Object> values = redis.eval(CAS_GET, Arrays.asList(key, CAS_KEY_PREFIX + key), null);
		if (CollectionUtils.isEmpty(values) || values.size() != 2) {
			return null;
		}

		Object value = values.get(0);
		long verion = new AnyValue(values.get(1)).getAsLongValue();
		return new CAS<>(verion, value);
	}

	public void set(String key, Object value, int exp) {
		if (exp > 0) {
			client.eval(SET_EXP, Arrays.asList(key, CAS_KEY_PREFIX + key, exp + ""), Arrays.asList(value));
		} else {
			client.eval(SET, Arrays.asList(key, CAS_KEY_PREFIX + key), Arrays.asList(value));
		}
	}

	public boolean delete(String key) {
		client.eval(DELETE, Arrays.asList(key, CAS_KEY_PREFIX + key), null);
		return true;
	}

	public boolean add(String key, Object value, int exp) {
		Object v;
		if (exp > 0) {
			v = client.eval(ADD_EXP, Arrays.asList(key, CAS_KEY_PREFIX + key, exp + ""), Arrays.asList(value));
		} else {
			v = client.eval(ADD, Arrays.asList(key, CAS_KEY_PREFIX + key), Arrays.asList(value));
		}
		return new AnyValue(v).getAsBooleanValue();
	}

}
