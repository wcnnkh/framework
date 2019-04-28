package scw.data.redis.jedis.cluster;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.JedisCluster;
import scw.data.redis.AbstractStringRedisOperations;
import scw.data.redis.RedisUtils;
import scw.data.redis.ResourceManager;

public abstract class AbstractClusterStringOperations extends AbstractStringRedisOperations
		implements ResourceManager<JedisCluster> {

	public String get(String key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.get(key);
		} finally {
			close(jedisCluster);
		}
	}

	public Boolean set(String key, String value) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return RedisUtils.isOK(jedisCluster.set(key, value));
		} finally {
			close(jedisCluster);
		}
	}

	public long setnx(String key, String value) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.setnx(key, value);
		} finally {
			close(jedisCluster);
		}
	}

	public Boolean setex(String key, int seconds, String value) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return RedisUtils.isOK(jedisCluster.setex(key, seconds, value));
		} finally {
			close(jedisCluster);
		}
	}

	public Boolean exists(String key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.exists(key);
		} finally {
			close(jedisCluster);
		}
	}

	public Long expire(String key, int seconds) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.expire(key, seconds);
		} finally {
			close(jedisCluster);
		}
	}

	public Long del(String key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.del(key);
		} finally {
			close(jedisCluster);
		}
	}

	public Long hset(String key, String field, String value) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.del(key);
		} finally {
			close(jedisCluster);
		}
	}

	public Long hsetnx(String key, String field, String value) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.hsetnx(key, field, value);
		} finally {
			close(jedisCluster);
		}
	}

	public Long hdel(String key, String... fields) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.hdel(key, fields);
		} finally {
			close(jedisCluster);
		}
	}

	public Boolean hexists(String key, String field) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.hexists(key, field);
		} finally {
			close(jedisCluster);
		}
	}

	public Long ttl(String key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.ttl(key);
		} finally {
			close(jedisCluster);
		}
	}

	public Long incr(String key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.incr(key);
		} finally {
			close(jedisCluster);
		}
	}

	public Long decr(String key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.decr(key);
		} finally {
			close(jedisCluster);
		}
	}

	public Collection<String> hvals(String key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.hvals(key);
		} finally {
			close(jedisCluster);
		}
	}

	public String hget(String key, String field) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.hget(key, field);
		} finally {
			close(jedisCluster);
		}
	}

	public Collection<String> hmget(String key, String... fields) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.hmget(key, fields);
		} finally {
			close(jedisCluster);
		}
	}

	public Long lpush(String key, String... values) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.lpush(key, values);
		} finally {
			close(jedisCluster);
		}
	}

	public Long rpush(String key, String... values) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.rpush(key, values);
		} finally {
			close(jedisCluster);
		}
	}

	public String rpop(String key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.rpop(key);
		} finally {
			close(jedisCluster);
		}
	}

	public String lpop(String key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.lpop(key);
		} finally {
			close(jedisCluster);
		}
	}

	public Set<String> smembers(String key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.smembers(key);
		} finally {
			close(jedisCluster);
		}
	}

	public Long srem(String key, String... members) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.srem(key, members);
		} finally {
			close(jedisCluster);
		}
	}

	public Long sadd(String key, String... members) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.sadd(key, members);
		} finally {
			close(jedisCluster);
		}
	}

	public Long zadd(String key, long score, String member) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.zadd(key, score, member);
		} finally {
			close(jedisCluster);
		}
	}

	public Boolean set(String key, String value, String nxxx, String expx, long time) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return RedisUtils.isOK(jedisCluster.set(key, value, nxxx, expx, time));
		} finally {
			close(jedisCluster);
		}
	}

	public Boolean sIsMember(String key, String member) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.sismember(key, member);
		} finally {
			close(jedisCluster);
		}
	}

	public String lindex(String key, int index) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.lindex(key, index);
		} finally {
			close(jedisCluster);
		}
	}

	public Long llen(String key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.llen(key);
		} finally {
			close(jedisCluster);
		}
	}

	public Object eval(String script, List<String> keys, List<String> args) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.eval(script, keys, args);
		} finally {
			close(jedisCluster);
		}
	}

	public Map<String, String> hgetAll(String key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.hgetAll(key);
		} finally {
			close(jedisCluster);
		}
	}

	public List<String> brpop(int timeout, String key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.brpop(timeout, key);
		} finally {
			close(jedisCluster);
		}
	}

	public List<String> blpop(int timeout, String key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.blpop(timeout, key);
		} finally {
			close(jedisCluster);
		}
	}

	public List<String> mget(String... keys) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.mget(keys);
		} finally {
			close(jedisCluster);
		}
	}
}
