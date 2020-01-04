package scw.data.redis;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import scw.core.string.StringCodec;
import scw.core.utils.CollectionUtils;
import scw.data.cas.CAS;
import scw.data.cas.CASOperations;
import scw.data.cas.SimpleCAS;
import scw.serializer.Serializer;

public class RedisCASOperations implements CASOperations {
	private static final String CAS_IS_NULL = "if (" + RedisUtils.isNullScript("cas") + ") then cas = 0 end";

	private static final String CAS_KEY_PREFIX = "cas_";
	private static final String CAS_EXP_SCRIPT = "local cas = redis.call('get', KEYS[2]) " + CAS_IS_NULL + " if ("
			+ RedisUtils.notNullScript("(KEYS[3] == cas)")
			+ ") then redis.call('set', KEYS[1], ARGV[1], 'EX', KEYS[4]) if redis.call('exists', KEYS[2]) == 1 then redis.call('incr', KEYS[2]) else redis.call('set', KEYS[2], 1, 'EX', KEYS[4]) end return 1 else return 0 end";
	private static final String CAS_SCRIPT = "local cas = redis.call('get', KEYS[2]) " + CAS_IS_NULL + " if ("
			+ RedisUtils.notNullScript("(KEYS[3] == cas)")
			+ ") then redis.call('set', KEYS[1], ARGV[1]) redis.call('incr', KEYS[2]) return 1 else return 0 end";
	private static final String CAS_DELETE = "local cas = redis.call('get', KEYS[2]) " + CAS_IS_NULL + " if ("
			+ RedisUtils.notNullScript("(ARGV[2] == cas)")
			+ ") then redis.call('del', KEYS[1]) redis.call('del', KEYS[2])  return 1 else return 0 end";
	private static final String DELETE = "redis.call('del', KEYS[1]) redis.call('del', KEYS[2])";
	private static final String CAS_GET = "if redis.call('exists', KEYS[1]) == 1 then local cas = redis.call('get', KEYS[2]) "
			+ CAS_IS_NULL
			+ " local res = {} res[1] = redis.call('get', KEYS[1]) res[2] = cas return res else return nil end";
	private static final String ADD_EXP = "if redis.call('exists', KEYS[1]) == 1 then return 0 else redis.call('set', KEYS[1], ARGV[1], 'EX', KEYS[3]) if redis.call('exists', KEYS[2]) == 1 then redis.call('incr', KEYS[2]) else redis.call('set', KEYS[2], 1, 'EX', KEYS[3]) end return 0  end";
	private static final String ADD = "if redis.call('exists', KEYS[1]) == 1 then return 0 else redis.call('set', KEYS[1], ARGV[1]) redis.call('incr', KEYS[2], 1) return 0  end";
	private static final String SET_EXP = "redis.call('set', KEYS[1], ARGV[1], 'EX', KEYS[3]) if redis.call('exists', KEYS[2]) == 1 then redis.call('incr', KEYS[2]) else redis.call('set', KEYS[2], 1, 'EX', KEYS[3]) end";
	private static final String SET = "redis.call('set', KEYS[1], ARGV[1]) redis.call('incr', KEYS[2], 1) end";
	private RedisOperations<String, Object> objectOperations;
	private StringCodec stringCodec;
	private Serializer serializer;

	public RedisCASOperations(RedisOperations<String, Object> objectOperations, Serializer serializer,
			StringCodec stringCodec) {
		this.objectOperations = objectOperations;
		this.stringCodec = stringCodec;
		this.serializer = serializer;
	}

	public boolean cas(String key, Object value, int exp, long cas) {
		long v;
		if (exp > 0) {
			v = (Long) objectOperations.eval(CAS_EXP_SCRIPT,
					Arrays.asList(key, CAS_KEY_PREFIX + key, cas + "", exp + ""), Arrays.asList(value));
		} else {
			v = (Long) objectOperations.eval(CAS_SCRIPT, Arrays.asList(key, CAS_KEY_PREFIX + key, cas + ""),
					Arrays.asList(value));
		}
		return v == 1;
	}

	public boolean delete(String key, long cas) {
		long v = (Long) objectOperations.eval(CAS_DELETE, Arrays.asList(key, CAS_KEY_PREFIX + key, cas + ""), null);
		return v == 1;
	}

	@SuppressWarnings("unchecked")
	public <T> CAS<T> get(String key) {
		List<Object> list = (List<Object>) objectOperations.eval(CAS_GET, Arrays.asList(key, CAS_KEY_PREFIX + key),
				null);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		byte[] v = (byte[]) list.get(0);
		return new SimpleCAS<T>(Long.parseLong(stringCodec.decode((byte[]) list.get(1))), v == null ? null : (T) serializer.deserialize(v));
	}

	public void set(String key, Object value, int exp) {
		if (exp > 0) {
			objectOperations.eval(SET_EXP, Arrays.asList(key, CAS_KEY_PREFIX + key, exp + ""), Arrays.asList(value));
		} else {
			objectOperations.eval(SET, Arrays.asList(key, CAS_KEY_PREFIX + key), Arrays.asList(value));
		}
	}

	public boolean delete(String key) {
		long v = (Long) objectOperations.eval(DELETE, Arrays.asList(key, CAS_KEY_PREFIX + key), null);
		return v == 1;
	}

	public boolean add(String key, Object value, int exp) {
		long v;
		if (exp > 0) {
			v = (Long) objectOperations.eval(ADD_EXP, Arrays.asList(key, CAS_KEY_PREFIX + key, exp + ""),
					Arrays.asList(value));
		} else {
			v = (Long) objectOperations.eval(ADD, Arrays.asList(key, CAS_KEY_PREFIX + key), Arrays.asList(value));
		}
		return v == 1;
	}

	// TODO 后面改成lua脚本
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
