package scw.data.redis.jedis;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.BinaryJedisCommands;
import scw.data.redis.BinaryCommands;
import scw.data.redis.RedisUtils;
import scw.data.redis.ResourceManager;

public abstract class AbstractBinaryCommands implements BinaryCommands, ResourceManager<BinaryJedisCommands> {

	public byte[] get(byte[] key) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.get(key);
		} finally {
			close(commands);
		}
	}

	public String set(byte[] key, byte[] value) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.set(key, value);
		} finally {
			close(commands);
		}
	}

	public Long setnx(byte[] key, byte[] value) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.setnx(key, value);
		} finally {
			close(commands);
		}
	}

	public String setex(byte[] key, int seconds, byte[] value) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.setex(key, seconds, value);
		} finally {
			close(commands);
		}
	}

	public Boolean exists(byte[] key) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.exists(key);
		} finally {
			close(commands);
		}
	}

	public Long expire(byte[] key, int seconds) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.expire(key, seconds);
		} finally {
			close(commands);
		}
	}

	public Long del(byte[] key) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.del(key);
		} finally {
			close(commands);
		}
	}

	public Long hset(byte[] key, byte[] field, byte[] value) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.hset(key, field, value);
		} finally {
			close(commands);
		}
	}

	public Long hsetnx(byte[] key, byte[] field, byte[] value) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.hsetnx(key, field, value);
		} finally {
			close(commands);
		}
	}

	public Long hdel(byte[] key, byte[]... fields) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.hdel(key, fields);
		} finally {
			close(commands);
		}
	}

	public Boolean hexists(byte[] key, byte[] field) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.hexists(key, field);
		} finally {
			close(commands);
		}
	}

	public Long ttl(byte[] key) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.ttl(key);
		} finally {
			close(commands);
		}
	}

	public Long incr(byte[] key) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.incr(key);
		} finally {
			close(commands);
		}
	}

	public Long decr(byte[] key) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.decr(key);
		} finally {
			close(commands);
		}
	}

	public Collection<byte[]> hvals(byte[] key) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.hvals(key);
		} finally {
			close(commands);
		}
	}

	public byte[] hget(byte[] key, byte[] field) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.hget(key, field);
		} finally {
			close(commands);
		}
	}

	public List<byte[]> hmget(byte[] key, byte[]... fields) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.hmget(key, fields);
		} finally {
			close(commands);
		}
	}

	public Long lpush(byte[] key, byte[]... value) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.lpush(key, value);
		} finally {
			close(commands);
		}
	}

	public Long rpush(byte[] key, byte[]... value) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.rpush(key, value);
		} finally {
			close(commands);
		}
	}

	public byte[] rpop(byte[] key) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.rpop(key);
		} finally {
			close(commands);
		}
	}

	public byte[] lpop(byte[] key) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.lpop(key);
		} finally {
			close(commands);
		}
	}

	public Set<byte[]> smembers(byte[] key) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.smembers(key);
		} finally {
			close(commands);
		}
	}

	public Long srem(byte[] key, byte[]... member) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.srem(key, member);
		} finally {
			close(commands);
		}
	}

	public Long sadd(byte[] key, byte[]... members) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.sadd(key, members);
		} finally {
			close(commands);
		}
	}

	public Long zadd(byte[] key, double score, byte[] member) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.zadd(key, score, member);
		} finally {
			close(commands);
		}
	}

	public boolean set(byte[] key, byte[] value, byte[] nxxx, byte[] expx, long time) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return RedisUtils.isOK(commands.set(key, value, nxxx, expx, time));
		} finally {
			close(commands);
		}
	}

	public Boolean sIsMember(byte[] key, byte[] member) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.sismember(key, member);
		} finally {
			close(commands);
		}
	}

	public byte[] lindex(byte[] key, int index) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.lindex(key, index);
		} finally {
			close(commands);
		}
	}

	public Long llen(byte[] key) {
		BinaryJedisCommands commands = null;
		try {
			commands = getResource();
			return commands.llen(key);
		} finally {
			close(commands);
		}
	}

}
