package scw.data.redis.jedis;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Jedis;
import scw.data.redis.ResourceManager;
import scw.data.redis.enums.EXPX;
import scw.data.redis.enums.NXXX;
import scw.data.redis.operations.AbstractStringRedisOperations;

public abstract class AbstractJedisStringOperations extends AbstractStringRedisOperations
		implements ResourceManager<Jedis> {

	public String get(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.get(key);
		} finally {
			close(jedis);
		}
	}

	public void set(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			JedisUtils.isOK(jedis.set(key, value));
		} finally {
			close(jedis);
		}
	}

	public boolean setnx(String key, String value) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.setnx(key, value) == 1;
		} finally {
			close(jedis);
		}
	}

	public void setex(String key, int seconds, String value) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			jedis.setex(key, seconds, value);
		} finally {
			close(jedis);
		}
	}

	public boolean exists(String key) {
		Jedis jedis = null;
		Boolean b;
		try {
			jedis = getResource();
			b = jedis.exists(key);
			return b == null ? false : b;
		} finally {
			close(jedis);
		}
	}

	public Long expire(String key, int seconds) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.expire(key, seconds);
		} finally {
			close(jedis);
		}
	}

	public boolean del(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.del(key) == 1;
		} finally {
			close(jedis);
		}
	}

	public Long hset(String key, String field, String value) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.hset(key, field, value);
		} finally {
			close(jedis);
		}
	}

	public Long hsetnx(String key, String field, String value) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.hsetnx(key, field, value);
		} finally {
			close(jedis);
		}
	}

	public Long hdel(String key, String... fields) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.hdel(key, fields);
		} finally {
			close(jedis);
		}
	}

	public Boolean hexists(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.hexists(key, field);
		} finally {
			close(jedis);
		}
	}

	public Long ttl(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.ttl(key);
		} finally {
			close(jedis);
		}
	}

	public Long incr(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.incr(key);
		} finally {
			close(jedis);
		}
	}

	public Long decr(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.decr(key);
		} finally {
			close(jedis);
		}
	}

	public Collection<String> hvals(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.hvals(key);
		} finally {
			close(jedis);
		}
	}

	public String hget(String key, String field) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.hget(key, field);
		} finally {
			close(jedis);
		}
	}

	public Collection<String> hmget(String key, String... fields) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.hmget(key, fields);
		} finally {
			close(jedis);
		}
	}

	public Long lpush(String key, String... values) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.lpush(key, values);
		} finally {
			close(jedis);
		}
	}

	public Long rpush(String key, String... values) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.rpush(key, values);
		} finally {
			close(jedis);
		}
	}

	public String rpop(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.rpop(key);
		} finally {
			close(jedis);
		}
	}

	public String lpop(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.lpop(key);
		} finally {
			close(jedis);
		}
	}

	public Set<String> smembers(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.smembers(key);
		} finally {
			close(jedis);
		}
	}

	public Long srem(String key, String... members) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.srem(key, members);
		} finally {
			close(jedis);
		}
	}

	public Long sadd(String key, String... members) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.sadd(key, members);
		} finally {
			close(jedis);
		}
	}

	public Long zadd(String key, long score, String member) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.zadd(key, score, member);
		} finally {
			close(jedis);
		}
	}

	public Boolean set(String key, String value, NXXX nxxx, EXPX expx, long time) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return JedisUtils.isOK(jedis.set(key, value, JedisUtils.parseSetParams(nxxx, expx, time)));
		} finally {
			close(jedis);
		}
	}

	public Boolean sIsMember(String key, String member) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.sismember(key, member);
		} finally {
			close(jedis);
		}
	}

	public String lindex(String key, int index) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return lindex(key, index);
		} finally {
			close(jedis);
		}
	}

	public Long llen(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.llen(key);
		} finally {
			close(jedis);
		}
	}

	@SuppressWarnings("unchecked")
	public Object eval(String script, List<String> keys, List<String> args) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.eval(script, keys == null ? Collections.EMPTY_LIST : keys,
					args == null ? Collections.EMPTY_LIST : args);
		} finally {
			close(jedis);
		}
	}

	public Map<String, String> hgetAll(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.hgetAll(key);
		} finally {
			close(jedis);
		}
	}

	public List<String> brpop(int timeout, String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.brpop(timeout, key);
		} finally {
			close(jedis);
		}
	}

	public List<String> blpop(int timeout, String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.blpop(timeout, key);
		} finally {
			close(jedis);
		}
	}

	public List<String> mget(String... keys) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.mget(keys);
		} finally {
			close(jedis);
		}
	}

	public Long hlen(String key) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return jedis.hlen(key);
		} finally {
			close(jedis);
		}
	}

	public Boolean hmset(String key, Map<String, String> hash) {
		Jedis jedis = null;
		try {
			jedis = getResource();
			return JedisUtils.isOK(jedis.hmset(key, hash));
		} finally {
			close(jedis);
		}
	}

	public Map<String, String> mget(Collection<String> keys) {
		if (keys == null || keys.isEmpty()) {
			return null;
		}

		List<String> list = mget(keys.toArray(new String[keys.size()]));
		if (list == null || list.isEmpty()) {
			return null;
		}

		Map<String, String> map = new HashMap<String, String>(keys.size(), 1);
		Iterator<String> keyIterator = keys.iterator();
		Iterator<String> valueIterator = list.iterator();
		while (keyIterator.hasNext() && valueIterator.hasNext()) {
			String key = keyIterator.next();
			String value = valueIterator.next();
			if (key == null || value == null) {
				continue;
			}

			map.put(key, value);
		}
		return map;
	}
}
