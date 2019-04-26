package scw.data.redis.jedis;

import java.util.List;
import java.util.Map;

import redis.clients.jedis.JedisCommands;
import scw.data.redis.Commands;
import scw.data.redis.RedisUtils;
import scw.data.redis.ResourceManager;

public abstract class AbstractCommands implements Commands, ResourceManager<JedisCommands> {

	public String get(String key) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.get(key);
		} finally {
			close(jedisCommands);
		}
	}

	public String set(String key, String value) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.set(key, value);
		} finally {
			close(jedisCommands);
		}
	}

	public Long setnx(String key, String value) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.setnx(key, value);
		} finally {
			close(jedisCommands);
		}
	}

	public String setex(String key, int seconds, String value) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.setex(key, seconds, value);
		} finally {
			close(jedisCommands);
		}
	}

	public boolean set(String key, String value, String nxxx, String expx, long time) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return RedisUtils.isOK(jedisCommands.set(key, value, nxxx, expx, time));
		} finally {
			close(jedisCommands);
		}
	}

	public Boolean exists(String key) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.exists(key);
		} finally {
			close(jedisCommands);
		}
	}

	public Long expire(String key, int seconds) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.expire(key, seconds);
		} finally {
			close(jedisCommands);
		}
	}

	public Long del(String key) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.del(key);
		} finally {
			close(jedisCommands);
		}
	}

	public Long hset(String key, String field, String value) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.hset(key, field, value);
		} finally {
			close(jedisCommands);
		}
	}

	public Long hsetnx(String key, String field, String value) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.hsetnx(key, field, value);
		} finally {
			close(jedisCommands);
		}
	}

	public Map<String, String> hgetAll(String key) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.hgetAll(key);
		} finally {
			close(jedisCommands);
		}
	}

	public Long hdel(String key, String... fields) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.hdel(key, fields);
		} finally {
			close(jedisCommands);
		}
	}

	public Boolean hexists(String key, String field) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.hexists(key, field);
		} finally {
			close(jedisCommands);
		}
	}

	public Long ttl(String key) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.ttl(key);
		} finally {
			close(jedisCommands);
		}
	}

	public Long incr(String key) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.incr(key);
		} finally {
			close(jedisCommands);
		}
	}

	public Long decr(String key) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.decr(key);
		} finally {
			close(jedisCommands);
		}
	}

	public List<String> hvals(String key) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.hvals(key);
		} finally {
			close(jedisCommands);
		}
	}

	public String hget(String key, String field) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.hget(key, field);
		} finally {
			close(jedisCommands);
		}
	}

	public Long lpush(String key, String... value) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.lpush(key, value);
		} finally {
			close(jedisCommands);
		}
	}

	public Long rpush(String key, String... value) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.rpush(key, value);
		} finally {
			close(jedisCommands);
		}
	}

	public String rpop(String key) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.rpop(key);
		} finally {
			close(jedisCommands);
		}
	}

	public List<String> blpop(int timeout, String key) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.blpop(timeout, key);
		} finally {
			close(jedisCommands);
		}
	}

	public List<String> brpop(int timeout, String key) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.brpop(timeout, key);
		} finally {
			close(jedisCommands);
		}
	}

	public String lpop(String key) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.lpop(key);
		} finally {
			close(jedisCommands);
		}
	}

	public String lindex(String key, int index) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.lindex(key, index);
		} finally {
			close(jedisCommands);
		}
	}

	public Long llen(String key) {
		JedisCommands jedisCommands = null;
		try {
			jedisCommands = getResource();
			return jedisCommands.llen(key);
		} finally {
			close(jedisCommands);
		}
	}

}
