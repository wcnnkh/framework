package scw.redis;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;
import scw.beans.annotation.Destroy;
import scw.common.exception.ParameterException;
import scw.common.utils.ConfigUtils;
import scw.common.utils.PropertiesUtils;
import scw.common.utils.StringUtils;
import scw.core.Constants;

/**
 * redis集群
 * 
 * @author shuchaowen
 *
 */
public final class RedisByJedisCluster implements Redis {
	private final JedisCluster jedisCluster;

	public RedisByJedisCluster(String propertiesFile) {
		JedisPoolConfig config = RedisByJedisPool.createConfig(propertiesFile);
		Properties properties = ConfigUtils.getProperties(propertiesFile, Constants.DEFAULT_CHARSET.name());
		String host = PropertiesUtils.getProperty(properties, "host", "address");
		if (StringUtils.isEmpty(host)) {
			throw new ParameterException("请配置host参数");
		}

		String[] hosts = StringUtils.commonSplit(host);
		Set<HostAndPort> nodes = new HashSet<HostAndPort>();
		for (String h : hosts) {
			HostAndPort hostAndPort = HostAndPort.parseString(h);
			nodes.add(hostAndPort);
		}
		this.jedisCluster = new JedisCluster(nodes, config);
	}

	public JedisCluster getJedisCluster() {
		return jedisCluster;
	}

	public String get(String key) {
		return jedisCluster.get(key);
	}

	public byte[] get(byte[] key) {
		return jedisCluster.get(key);
	}

	public String set(String key, String value) {
		return jedisCluster.set(key, value);
	}

	public String set(byte[] key, byte[] value) {
		return jedisCluster.set(key, value);
	}

	public String setex(String key, int seconds, String value) {
		return jedisCluster.setex(key, seconds, value);
	}

	public String setex(byte[] key, int seconds, byte[] value) {
		return jedisCluster.setex(key, seconds, value);
	}

	public Boolean exists(String key) {
		return jedisCluster.exists(key);
	}

	public Boolean exists(byte[] key) {
		return jedisCluster.exists(key);
	}

	public Long expire(String key, int seconds) {
		return jedisCluster.expire(key, seconds);
	}

	public Long expire(byte[] key, int seconds) {
		return jedisCluster.expire(key, seconds);
	}

	public Long delete(String key) {
		return jedisCluster.del(key);
	}

	public Long delete(byte[] key) {
		return jedisCluster.del(key);
	}

	public Long delete(String... key) {
		return jedisCluster.del(key);
	}

	public Long delete(byte[]... key) {
		return jedisCluster.del(key);
	}

	public Long hset(String key, String field, String value) {
		return jedisCluster.hset(key, field, value);
	}

	public Long hset(byte[] key, byte[] field, byte[] value) {
		return jedisCluster.hset(key, field, value);
	}

	public Long hsetnx(String key, String field, String value) {
		return jedisCluster.hsetnx(key, field, value);
	}

	public Map<String, String> hgetAll(String key) {
		return jedisCluster.hgetAll(key);
	}

	public Long hsetnx(byte[] key, byte[] field, byte[] value) {
		return jedisCluster.hsetnx(key, field, value);
	}

	public List<String> mget(String... key) {
		return jedisCluster.mget(key);
	}

	public List<byte[]> mget(byte[]... key) {
		return jedisCluster.mget(key);
	}

	public Long hdel(String key, String... fields) {
		return jedisCluster.hdel(key, fields);
	}

	public Long hdel(byte[] key, byte[]... fields) {
		return jedisCluster.hdel(key, fields);
	}

	public Boolean hexists(String key, String field) {
		return jedisCluster.hexists(key, field);
	}

	public Boolean hexists(byte[] key, byte[] field) {
		return jedisCluster.hexists(key, field);
	}

	public Long ttl(byte[] key) {
		return jedisCluster.ttl(key);
	}

	public Long ttl(String key) {
		return jedisCluster.ttl(key);
	}

	public Long setnx(String key, String value) {
		return jedisCluster.setnx(key, value);
	}

	public Long setnx(byte[] key, byte[] value) {
		return jedisCluster.setnx(key, value);
	}

	public Long incr(String key) {
		return jedisCluster.incr(key);
	}

	public Long incr(byte[] key) {
		return jedisCluster.incr(key);
	}

	public Long decr(String key) {
		return jedisCluster.decr(key);
	}

	public Long decr(byte[] key) {
		return jedisCluster.decr(key);
	}

	public List<String> hvals(String key) {
		return jedisCluster.hvals(key);
	}

	public Collection<byte[]> hvals(byte[] key) {
		return jedisCluster.hvals(key);
	}

	public String hget(String key, String field) {
		return jedisCluster.hget(key, field);
	}

	public byte[] hget(byte[] key, byte[] field) {
		return jedisCluster.hget(key, field);
	}

	public Long lpush(String key, String... value) {
		return jedisCluster.lpush(key, value);
	}

	public Long lpush(byte[] key, byte[]... value) {
		return jedisCluster.lpush(key, value);
	}

	public String rpop(String key) {
		return jedisCluster.rpop(key);
	}

	public byte[] rpop(byte[] key) {
		return jedisCluster.rpop(key);
	}

	public List<String> brpop(int timeout, String... key) {
		return jedisCluster.brpop(timeout, key);
	}

	public List<byte[]> brpop(int timeout, byte[]... key) {
		return jedisCluster.brpop(timeout, key);
	}

	public boolean set(String key, String value, String nxxx, String expx, long time) {
		return SUCCESS.equals(jedisCluster.set(key, value, nxxx, expx, time));
	}

	public boolean set(byte[] key, byte[] value, byte[] nxxx, byte[] expx, long time) {
		return SUCCESS.equals(jedisCluster.set(key, value, nxxx, expx, time));
	}

	public Object eval(String script, List<String> keys, List<String> args) {
		return jedisCluster.eval(script, keys, args);
	}

	public List<byte[]> blpop(int timeout, byte[]... key) {
		return jedisCluster.blpop(timeout, key);
	}

	public String lpop(String key) {
		return jedisCluster.lpop(key);
	}

	public byte[] lpop(byte[] key) {
		return jedisCluster.lpop(key);
	}

	public Long zadd(byte[] key, double score, byte[] member) {
		return jedisCluster.zadd(key, score, member);
	}

	public Long sadd(byte[] key, byte[]... members) {
		return jedisCluster.sadd(key, members);
	}

	public Long srem(byte[] key, byte[]... member) {
		return jedisCluster.srem(key, member);
	}

	public Set<byte[]> smembers(byte[] key) {
		return jedisCluster.smembers(key);
	}

	public Boolean sIsMember(byte[] key, byte[] member) {
		return jedisCluster.sismember(key, member);
	}

	public List<byte[]> hmget(byte[] key, byte[]... fields) {
		return jedisCluster.hmget(key, fields);
	}

	public Long rpush(String key, String... value) {
		return jedisCluster.rpush(key, value);
	}

	public Long rpush(byte[] key, byte[]... value) {
		return jedisCluster.rpush(key, value);
	}

	public List<String> blpop(int timeout, String... key) {
		return jedisCluster.blpop(timeout, key);
	}

	public byte[] rpoplpush(byte[] srckey, byte[] dstkey) {
		return jedisCluster.rpoplpush(srckey, dstkey);
	}

	public String rpoplpush(String srckey, String dstkey) {
		return jedisCluster.rpoplpush(srckey, dstkey);
	}

	public String brpoplpush(String source, String destination, int timeout) {
		return jedisCluster.brpoplpush(source, destination, timeout);
	}

	public byte[] brpoplpush(byte[] source, byte[] destination, int timeout) {
		return jedisCluster.brpoplpush(source, destination, timeout);
	}

	public String lindex(String key, int index) {
		return jedisCluster.lindex(key, index);
	}

	public byte[] lindex(byte[] key, int index) {
		return jedisCluster.lindex(key, index);
	}

	public Long llen(String key) {
		return jedisCluster.llen(key);
	}

	public Long llen(byte[] key) {
		return jedisCluster.llen(key);
	}

	@Destroy
	public void destroy() throws IOException {
		jedisCluster.close();
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
		return (Long) jedisCluster.eval(INCR_AND_INIT_SCRIPT, Arrays.asList(key),
				Arrays.asList(String.valueOf(incr), String.valueOf(initValue)));
	}

	public long decr(String key, long decr, long initValue) {
		return (Long) jedisCluster.eval(DECR_AND_INIT_SCRIPT, Arrays.asList(key),
				Arrays.asList(String.valueOf(decr), String.valueOf(initValue)));
	}
}
