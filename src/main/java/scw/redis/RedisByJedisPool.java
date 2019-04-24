package scw.redis;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import scw.beans.annotation.Bean;
import scw.beans.annotation.Destroy;
import scw.common.utils.ConfigUtils;
import scw.common.utils.PropertiesUtils;
import scw.common.utils.StringUtils;
import scw.core.Constants;

@Bean(proxy = false)
public class RedisByJedisPool implements Redis {

	private final JedisPool jedisPool;
	private final String auth;

	public RedisByJedisPool(String propertiesFile) {
		JedisPoolConfig config = createConfig(propertiesFile);

		Properties properties = ConfigUtils.getProperties(propertiesFile, Constants.DEFAULT_CHARSET.name());
		String host = PropertiesUtils.getProperty(properties, "host", "address");
		String port = PropertiesUtils.getProperty(properties, "port");
		this.auth = PropertiesUtils.getProperty(properties, "auth", "password", "pwd");
		if (StringUtils.isEmpty(port)) {
			this.jedisPool = new JedisPool(config, host);
		} else {
			this.jedisPool = new JedisPool(config, host, Integer.parseInt(port));
		}
	}

	public static JedisPoolConfig createConfig(String propertiesFile) {
		JedisPoolConfig config = new JedisPoolConfig();
		PropertiesUtils.loadProperties(config, propertiesFile, Arrays.asList("maxWait,maxWaitMillis"));
		return config;
	}

	public RedisByJedisPool() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(512);
		jedisPoolConfig.setMaxIdle(200);
		jedisPoolConfig.setTestOnBorrow(true);
		this.jedisPool = new JedisPool(jedisPoolConfig, "localhost");
		this.auth = null;
	}

	public RedisByJedisPool(int maxTotal, int maxIdle, boolean testOnBorrow, String host) {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(maxTotal);
		jedisPoolConfig.setMaxIdle(maxIdle);
		jedisPoolConfig.setTestOnBorrow(testOnBorrow);
		this.jedisPool = new JedisPool(jedisPoolConfig, host);
		this.auth = null;
	}

	public JedisPool getJedisPool() {
		return jedisPool;
	}

	protected void before(Jedis jedis) {
		if (auth != null) {
			jedis.auth(auth);
		}
	}

	public String get(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.get(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public byte[] get(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.get(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public String set(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.set(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public String set(byte[] key, byte[] value) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.set(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public String setex(String key, int seconds, String value) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.setex(key, seconds, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public String setex(byte[] key, int seconds, byte[] value) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.setex(key, seconds, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Boolean exists(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.exists(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Boolean exists(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.exists(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long expire(String key, int seconds) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.expire(key, seconds);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long expire(byte[] key, int seconds) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.expire(key, seconds);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long delete(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.del(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long delete(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.del(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long delete(String... key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.del(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long delete(byte[]... key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.del(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long hset(String key, String field, String value) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.hset(key, field, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long hset(byte[] key, byte[] field, byte[] value) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.hset(key, field, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long hsetnx(String key, String field, String value) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.hsetnx(key, field, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Map<String, String> hgetAll(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.hgetAll(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long hsetnx(byte[] key, byte[] field, byte[] value) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.hsetnx(key, field, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public List<String> mget(String... key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.mget(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public List<byte[]> mget(byte[]... key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.mget(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long hdel(String key, String... fields) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.hdel(key, fields);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long hdel(byte[] key, byte[]... fields) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.hdel(key, fields);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Boolean hexists(String key, String field) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.hexists(key, field);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Boolean hexists(byte[] key, byte[] field) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.hexists(key, field);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long ttl(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.ttl(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long ttl(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.ttl(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long setnx(String key, String value) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.setnx(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long setnx(byte[] key, byte[] value) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.setnx(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long incr(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.incr(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long incr(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.incr(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long decr(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.decr(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long decr(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.decr(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public List<String> hvals(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.hvals(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Collection<byte[]> hvals(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.hvals(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public String hget(String key, String field) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.hget(key, field);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public byte[] hget(byte[] key, byte[] field) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.hget(key, field);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long lpush(String key, String... value) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.lpush(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long lpush(byte[] key, byte[]... value) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.lpush(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public String rpop(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.rpop(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public byte[] rpop(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.rpop(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public List<String> brpop(int timeout, String... key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.brpop(timeout, key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public List<byte[]> brpop(int timeout, byte[]... key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.brpop(timeout, key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public boolean set(String key, String value, String nxxx, String expx, long time) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return SUCCESS.equals(jedis.set(key, value, nxxx, expx, time));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public boolean set(byte[] key, byte[] value, byte[] nxxx, byte[] expx, long time) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return SUCCESS.equals(jedis.set(key, value, nxxx, expx, time));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Object eval(String script, List<String> keys, List<String> args) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.eval(script, keys, args);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public List<byte[]> blpop(int timeout, byte[]... key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.blpop(timeout, key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public String lpop(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.lpop(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public byte[] lpop(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.lpop(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long zadd(byte[] key, double score, byte[] member) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.zadd(key, score, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long sadd(byte[] key, byte[]... members) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.sadd(key, members);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long srem(byte[] key, byte[]... member) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.srem(key, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Set<byte[]> smembers(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.smembers(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Boolean sIsMember(byte[] key, byte[] member) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.sismember(key, member);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public List<byte[]> hmget(byte[] key, byte[]... fields) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.hmget(key, fields);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long rpush(String key, String... value) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.rpush(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long rpush(byte[] key, byte[]... value) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.rpush(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public List<String> blpop(int timeout, String... key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.blpop(timeout, key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public byte[] rpoplpush(byte[] srckey, byte[] dstkey) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.rpoplpush(srckey, dstkey);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public String rpoplpush(String srckey, String dstkey) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.rpoplpush(srckey, dstkey);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public String brpoplpush(String source, String destination, int timeout) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.brpoplpush(source, destination, timeout);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public byte[] brpoplpush(byte[] source, byte[] destination, int timeout) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.brpoplpush(source, destination, timeout);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public String lindex(String key, int index) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.lindex(key, index);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public byte[] lindex(byte[] key, int index) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.lindex(key, index);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long llen(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.llen(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long llen(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return jedis.llen(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	@Destroy
	public void destroy() {
		jedisPool.close();
	}

	public String getAndTouch(String key, int exp) {
		String v = get(key);
		if (v != null) {
			expire(key, exp);
		}
		return v;
	}

	public byte[] getAndTouch(byte[] key, int exp) {
		byte[] v = get(key);
		if (v != null) {
			expire(key, exp);
		}
		return v;
	}

	public Map<String, String> get(String... key) {
		List<String> list = mget(key);
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
		List<byte[]> list = mget(key);
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
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return (Long) jedis.eval(INCR_AND_INIT_SCRIPT, Arrays.asList(key),
					Arrays.asList(String.valueOf(incr), String.valueOf(initValue)));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public long decr(String key, long decr, long initValue) {
		Jedis jedis = jedisPool.getResource();
		try {
			before(jedis);
			return (Long) jedis.eval(DECR_AND_INIT_SCRIPT, Arrays.asList(key),
					Arrays.asList(String.valueOf(decr), String.valueOf(initValue)));
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
}
