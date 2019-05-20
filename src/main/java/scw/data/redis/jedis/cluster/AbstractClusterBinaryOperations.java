package scw.data.redis.jedis.cluster;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.JedisCluster;
import scw.data.redis.AbstractBinaryRedisOperations;
import scw.data.redis.RedisUtils;
import scw.data.redis.ResourceManager;

public abstract class AbstractClusterBinaryOperations extends AbstractBinaryRedisOperations
		implements ResourceManager<JedisCluster> {

	public byte[] get(byte[] key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.get(key);
		} finally {
			close(jedisCluster);
		}
	}

	public void set(byte[] key, byte[] value) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			jedisCluster.set(key, value);
		} finally {
			close(jedisCluster);
		}
	}

	public boolean setnx(byte[] key, byte[] value) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.setnx(key, value) == 1;
		} finally {
			close(jedisCluster);
		}
	}

	public void setex(byte[] key, int seconds, byte[] value) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			jedisCluster.setex(key, seconds, value);
		} finally {
			close(jedisCluster);
		}
	}

	public boolean exists(byte[] key) {
		JedisCluster jedisCluster = null;
		Boolean b;
		try {
			jedisCluster = getResource();
			b = jedisCluster.exists(key);
			return b == null ? false : b;
		} finally {
			close(jedisCluster);
		}
	}

	public Long expire(byte[] key, int seconds) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.expire(key, seconds);
		} finally {
			close(jedisCluster);
		}
	}

	public boolean del(byte[] key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.del(key) == 1;
		} finally {
			close(jedisCluster);
		}
	}

	public Long hset(byte[] key, byte[] field, byte[] value) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.del(key);
		} finally {
			close(jedisCluster);
		}
	}

	public Long hsetnx(byte[] key, byte[] field, byte[] value) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.hsetnx(key, field, value);
		} finally {
			close(jedisCluster);
		}
	}

	public Long hdel(byte[] key, byte[]... fields) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.hdel(key, fields);
		} finally {
			close(jedisCluster);
		}
	}

	public Boolean hexists(byte[] key, byte[] field) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.hexists(key, field);
		} finally {
			close(jedisCluster);
		}
	}

	public Long ttl(byte[] key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.ttl(key);
		} finally {
			close(jedisCluster);
		}
	}

	public Long incr(byte[] key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.incr(key);
		} finally {
			close(jedisCluster);
		}
	}

	public Long decr(byte[] key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.decr(key);
		} finally {
			close(jedisCluster);
		}
	}

	public Collection<byte[]> hvals(byte[] key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.hvals(key);
		} finally {
			close(jedisCluster);
		}
	}

	public byte[] hget(byte[] key, byte[] field) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.hget(key, field);
		} finally {
			close(jedisCluster);
		}
	}

	public Collection<byte[]> hmget(byte[] key, byte[]... fields) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.hmget(key, fields);
		} finally {
			close(jedisCluster);
		}
	}

	public Long lpush(byte[] key, byte[]... values) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.lpush(key, values);
		} finally {
			close(jedisCluster);
		}
	}

	public Long rpush(byte[] key, byte[]... values) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.rpush(key, values);
		} finally {
			close(jedisCluster);
		}
	}

	public byte[] rpop(byte[] key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.rpop(key);
		} finally {
			close(jedisCluster);
		}
	}

	public byte[] lpop(byte[] key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.lpop(key);
		} finally {
			close(jedisCluster);
		}
	}

	public Set<byte[]> smembers(byte[] key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.smembers(key);
		} finally {
			close(jedisCluster);
		}
	}

	public Long srem(byte[] key, byte[]... members) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.srem(key, members);
		} finally {
			close(jedisCluster);
		}
	}

	public Long sadd(byte[] key, byte[]... members) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.sadd(key, members);
		} finally {
			close(jedisCluster);
		}
	}

	public Long zadd(byte[] key, long score, byte[] member) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.zadd(key, score, member);
		} finally {
			close(jedisCluster);
		}
	}

	public Boolean set(byte[] key, byte[] value, byte[] nxxx, byte[] expx, long time) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return RedisUtils.isOK(jedisCluster.set(key, value, nxxx, expx, time));
		} finally {
			close(jedisCluster);
		}
	}

	public Boolean sIsMember(byte[] key, byte[] member) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.sismember(key, member);
		} finally {
			close(jedisCluster);
		}
	}

	public byte[] lindex(byte[] key, int index) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.lindex(key, index);
		} finally {
			close(jedisCluster);
		}
	}

	public Long llen(byte[] key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.llen(key);
		} finally {
			close(jedisCluster);
		}
	}

	public Object eval(byte[] script, List<byte[]> keys, List<byte[]> args) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.eval(script, keys, args);
		} finally {
			close(jedisCluster);
		}
	}

	public Map<byte[], byte[]> hgetAll(byte[] key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.hgetAll(key);
		} finally {
			close(jedisCluster);
		}
	}

	public List<byte[]> brpop(int timeout, byte[] key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.brpop(timeout, key);
		} finally {
			close(jedisCluster);
		}
	}

	public List<byte[]> blpop(int timeout, byte[] key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.blpop(timeout, key);
		} finally {
			close(jedisCluster);
		}
	}

	public List<byte[]> mget(byte[]... keys) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.mget(keys);
		} finally {
			close(jedisCluster);
		}
	}

	public Long hlen(byte[] key) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return jedisCluster.hlen(key);
		} finally {
			close(jedisCluster);
		}
	}

	public Boolean hmset(byte[] key, Map<byte[], byte[]> hash) {
		JedisCluster jedisCluster = null;
		try {
			jedisCluster = getResource();
			return RedisUtils.isOK(jedisCluster.hmset(key, hash));
		} finally {
			close(jedisCluster);
		}
	}
}
