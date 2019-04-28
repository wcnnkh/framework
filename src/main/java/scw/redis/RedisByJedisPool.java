package scw.redis;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import scw.beans.annotation.Bean;

/**
 * 此类已弃用
 * @author shuchaowen
 *
 */
@Bean(proxy = false)
public class RedisByJedisPool extends scw.data.redis.jedis.RedisByJedisPool {

	public RedisByJedisPool(String propertiesFile) {
		super(propertiesFile);
	}

	public RedisByJedisPool() {
		super();
	}

	public RedisByJedisPool(int maxTotal, int maxIdle, boolean testOnBorrow, String host) {
		super(maxTotal, maxIdle, testOnBorrow, host);
	}

	public String get(String key) {
		return getStringOperations().get(key);
	}

	public byte[] get(byte[] key) {
		return getBinaryOperations().get(key);
	}

	public Boolean set(String key, String value) {
		return getStringOperations().set(key, value);
	}

	public Boolean set(byte[] key, byte[] value) {
		return getBinaryOperations().set(key, value);
	}

	public Boolean setex(String key, int seconds, String value) {
		return getStringOperations().setex(key, seconds, value);
	}

	public Boolean setex(byte[] key, int seconds, byte[] value) {
		return getBinaryOperations().setex(key, seconds, value);
	}

	public Boolean exists(String key) {
		return getStringOperations().exists(key);
	}

	public Boolean exists(byte[] key) {
		return getBinaryOperations().exists(key);
	}

	public Long expire(String key, int seconds) {
		return getStringOperations().expire(key, seconds);
	}

	public Long expire(byte[] key, int seconds) {
		return getBinaryOperations().expire(key, seconds);
	}

	public Long delete(String key) {
		return getStringOperations().del(key);
	}

	public Long delete(byte[] key) {
		return getBinaryOperations().del(key);
	}

	public Long hset(String key, String field, String value) {
		return getStringOperations().hset(key, field, value);
	}

	public Long hset(byte[] key, byte[] field, byte[] value) {
		return getBinaryOperations().hset(key, field, value);
	}

	public Long hsetnx(String key, String field, String value) {
		return getStringOperations().hsetnx(key, field, value);
	}

	public Map<String, String> hgetAll(String key) {
		return getStringOperations().hgetAll(key);
	}

	public Long hsetnx(byte[] key, byte[] field, byte[] value) {
		return getBinaryOperations().hsetnx(key, field, value);
	}

	public Long hdel(String key, String... fields) {
		return getStringOperations().hdel(key, fields);
	}

	public Long hdel(byte[] key, byte[]... fields) {
		return getBinaryOperations().hdel(key, fields);
	}

	public Boolean hexists(String key, String field) {
		return getStringOperations().hexists(key, field);
	}

	public Boolean hexists(byte[] key, byte[] field) {
		return getBinaryOperations().hexists(key, field);
	}

	public Long setnx(String key, String value) {
		return getStringOperations().setnx(key, value);
	}

	public Long setnx(byte[] key, byte[] value) {
		return getBinaryOperations().setnx(key, value);
	}

	public Long incr(String key) {
		return getStringOperations().incr(key);
	}

	public Long incr(byte[] key) {
		return getBinaryOperations().incr(key);
	}

	public Long decr(String key) {
		return getStringOperations().decr(key);
	}

	public Long decr(byte[] key) {
		return getBinaryOperations().decr(key);
	}

	public Collection<String> hvals(String key) {
		return getStringOperations().hvals(key);
	}

	public Collection<byte[]> hvals(byte[] key) {
		return getBinaryOperations().hvals(key);
	}

	public String hget(String key, String field) {
		return getStringOperations().hget(key, field);
	}

	public byte[] hget(byte[] key, byte[] field) {
		return getBinaryOperations().hget(key, field);
	}

	public Long lpush(String key, String... value) {
		return getStringOperations().lpush(key, value);
	}

	public Long lpush(byte[] key, byte[]... value) {
		return getBinaryOperations().lpush(key, value);
	}

	public String rpop(String key) {
		return getStringOperations().rpop(key);
	}

	public byte[] rpop(byte[] key) {
		return getBinaryOperations().rpop(key);
	}

	public Collection<String> brpop(int timeout, String key) {
		return getStringOperations().brpop(timeout, key);
	}

	public Collection<byte[]> brpop(int timeout, byte[] key) {
		return getBinaryOperations().brpop(timeout, key);
	}

	public boolean set(String key, String value, String nxxx, String expx, long time) {
		return getStringOperations().set(key, value, nxxx, expx, time);
	}

	public boolean set(byte[] key, byte[] value, byte[] nxxx, byte[] expx, long time) {
		return getBinaryOperations().set(key, value, nxxx, expx, time);
	}

	public Object eval(String script, List<String> keys, List<String> args) {
		return getStringOperations().eval(script, keys, args);
	}

	public Collection<byte[]> blpop(int timeout, byte[] key) {
		return getBinaryOperations().blpop(timeout, key);
	}

	public String lpop(String key) {
		return getStringOperations().lpop(key);
	}

	public byte[] lpop(byte[] key) {
		return getBinaryOperations().lpop(key);
	}

	public Collection<byte[]> hmget(byte[] key, byte[]... fields) {
		return getBinaryOperations().hmget(key, fields);
	}

	public Long rpush(String key, String... value) {
		return getStringOperations().rpush(key, value);
	}

	public Long rpush(byte[] key, byte[]... value) {
		return getBinaryOperations().rpush(key, value);
	}

	public Collection<String> blpop(int timeout, String key) {
		return getStringOperations().blpop(timeout, key);
	}

	public String getAndTouch(String key, int newExp) {
		return getStringOperations().getAndTouch(key, newExp);
	}

	public byte[] getAndTouch(byte[] key, int exp) {
		return getBinaryOperations().getAndTouch(key, exp);
	}

	public Map<String, String> get(String... key) {
		List<String> list = getStringOperations().mget(key);
		if (list == null || list.isEmpty()) {
			return null;
		}

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(list.size());
		for (int i = 0, size = list.size(); i < size; i++) {
			String value = list.get(i);
			if (value == null) {
				continue;
			}

			map.put(key[i], value);
		}
		return map;
	}

	public Map<byte[], byte[]> get(byte[]... key) {
		List<byte[]> list = getBinaryOperations().mget(key);
		if (list == null || list.isEmpty()) {
			return null;
		}

		LinkedHashMap<byte[], byte[]> map = new LinkedHashMap<byte[], byte[]>(list.size());
		for (int i = 0, size = list.size(); i < size; i++) {
			byte[] value = list.get(i);
			if (value == null) {
				continue;
			}

			map.put(key[i], value);
		}
		return map;
	}

	public long incr(String key, long incr, long initValue) {
		return getStringOperations().incr(key, incr, initValue);
	}

	public long decr(String key, long decr, long initValue) {
		return getStringOperations().decr(key, decr, initValue);
	}
}
