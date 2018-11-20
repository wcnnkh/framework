package shuchaowen.redis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisByJedisPool implements Redis {
	private static final String SUCCESS = "OK";

	private final JedisPool jedisPool;

	public RedisByJedisPool() {
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(512);
		jedisPoolConfig.setMaxIdle(200);
		jedisPoolConfig.setTestOnBorrow(true);
		this.jedisPool = new JedisPool(jedisPoolConfig, "localhost");
	}

	/**
	 * @param jedisPool
	 * @param abnormalInterruption
	 *            发生异常时是否中断
	 */
	public RedisByJedisPool(JedisPool jedisPool, boolean abnormalInterruption) {
		this.jedisPool = jedisPool;
	}

	public JedisPool getJedisPool() {
		return jedisPool;
	}

	public String get(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
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
			return jedis.hsetnx(key, field, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long hsetnx(byte[] key, byte[] field, byte[] value) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.hsetnx(key, field, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Map<String, String> get(String... key) {
		Map<String, String> map = new HashMap<String, String>();
		Jedis jedis = jedisPool.getResource();
		try {
			for (String k : key) {
				map.put(k, jedis.get(k));
			}
			return map;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Map<byte[], byte[]> get(byte[]... key) {
		Map<byte[], byte[]> map = new HashMap<byte[], byte[]>();
		Jedis jedis = jedisPool.getResource();
		try {
			for (byte[] k : key) {
				map.put(k, jedis.get(k));
			}
			return map;
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public Long hdel(String key, String... fields) {
		Jedis jedis = jedisPool.getResource();
		try {
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
			return jedis.hvals(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public List<byte[]> hvals(byte[] key) {
		Jedis jedis = jedisPool.getResource();
		try {
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
			return jedis.rpop(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public List<String> brpop(String... key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.brpop(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public List<byte[]> brpop(byte[]... key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.brpop(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public boolean set(String key, String value, String nxxx, String expx, long time) {
		Jedis jedis = jedisPool.getResource();
		try {
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
			return jedis.eval(script, keys, args);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public List<byte[]> blpop(byte[]... key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.blpop(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public String lpop(String key) {
		Jedis jedis = jedisPool.getResource();
		try {
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
			return jedis.rpush(key, value);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public List<String> blpop(String... key) {
		Jedis jedis = jedisPool.getResource();
		try {
			return jedis.blpop(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	public byte[] rpoplpush(byte[] srckey, byte[] dstkey) {
		Jedis jedis = jedisPool.getResource();
		try {
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
			return jedis.llen(key);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}
	
	public void destroy(){
		jedisPool.close();
	}
}
