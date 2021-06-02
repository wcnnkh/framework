package scw.redis.core.cas;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import scw.core.utils.CollectionUtils;
import scw.data.cas.CAS;
import scw.data.cas.CASOperations;
import scw.redis.core.RedisCommands;
import scw.value.AnyValue;

public class RedisCASOperations implements CASOperations {
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

	private RedisCommands<String, Object> redisCommands;

	public RedisCASOperations(RedisCommands<String, Object> redisCommands) {
		this.redisCommands = redisCommands;
	}

	public boolean cas(String key, Object value, int exp, long cas) {
		Object resposne;
		if (exp > 0) {
			resposne = redisCommands.eval(CAS_EXP_SCRIPT, Arrays.asList(key, CAS_KEY_PREFIX + key, cas + "", exp + ""),
					Arrays.asList(value));
		} else {
			resposne = redisCommands.eval(CAS_SCRIPT, Arrays.asList(key, CAS_KEY_PREFIX + key, cas + ""),
					Arrays.asList(value));
		}
		return new AnyValue(resposne).getAsBooleanValue();
	}

	public boolean delete(String key, long cas) {
		Object v = redisCommands.eval(CAS_DELETE, Arrays.asList(key, CAS_KEY_PREFIX + key, cas + ""), null);
		return new AnyValue(v).getAsBooleanValue();
	}

	@SuppressWarnings("unchecked")
	public <T> CAS<T> get(String key) {
		List<Object> values = redisCommands.eval(CAS_GET, Arrays.asList(key, CAS_KEY_PREFIX + key), null);
		if (CollectionUtils.isEmpty(values) || values.size() != 2) {
			return null;
		}

		T value = (T) values.get(0);
		long verion = new AnyValue(values.get(1)).getAsLongValue();
		return new CAS<T>(verion, value);
	}

	public void set(String key, Object value, int exp) {
		if (exp > 0) {
			redisCommands.eval(SET_EXP, Arrays.asList(key, CAS_KEY_PREFIX + key, exp + ""), Arrays.asList(value));
		} else {
			redisCommands.eval(SET, Arrays.asList(key, CAS_KEY_PREFIX + key), Arrays.asList(value));
		}
	}

	public boolean delete(String key) {
		redisCommands.eval(DELETE, Arrays.asList(key, CAS_KEY_PREFIX + key), null);
		return true;
	}

	public boolean add(String key, Object value, int exp) {
		Object v;
		if (exp > 0) {
			v = redisCommands.eval(ADD_EXP, Arrays.asList(key, CAS_KEY_PREFIX + key, exp + ""), Arrays.asList(value));
		} else {
			v = redisCommands.eval(ADD, Arrays.asList(key, CAS_KEY_PREFIX + key), Arrays.asList(value));
		}
		return new AnyValue(v).getAsBooleanValue();
	}

	@SuppressWarnings("unchecked")
	public <T> Map<String, CAS<T>> get(Collection<String> keys) {
		if (CollectionUtils.isEmpty(keys)) {
			return Collections.EMPTY_MAP;
		}

		Map<String, CAS<T>> map = new LinkedHashMap<String, CAS<T>>(keys.size());
		for (String key : keys) {
			CAS<T> v = get(key);
			if (v == null) {
				continue;
			}

			map.put(key, v);
		}
		return map;
	}

}
