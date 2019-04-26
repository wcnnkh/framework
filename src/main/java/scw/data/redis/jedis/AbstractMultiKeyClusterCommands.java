package scw.data.redis.jedis;

import java.util.List;
import java.util.Set;

import redis.clients.jedis.MultiKeyJedisClusterCommands;
import scw.data.redis.ResourceManager;

public abstract class AbstractMultiKeyClusterCommands
		implements scw.data.redis.MultiKeyCommands, ResourceManager<MultiKeyJedisClusterCommands> {

	public Long del(String... keys) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.del(keys);
		} finally {
			close(commands);
		}
	}

	public Long exists(String... keys) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.exists(keys);
		} finally {
			close(commands);
		}
	}

	public List<String> blpop(int timeout, String... keys) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.blpop(timeout, keys);
		} finally {
			close(commands);
		}
	}

	public List<String> brpop(int timeout, String... keys) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.brpop(timeout, keys);
		} finally {
			close(commands);
		}
	}

	public List<String> mget(String... keys) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.mget(keys);
		} finally {
			close(commands);
		}
	}

	public String mset(String... keysvalues) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.mset(keysvalues);
		} finally {
			close(commands);
		}
	}

	public Long msetnx(String... keysvalues) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.msetnx(keysvalues);
		} finally {
			close(commands);
		}
	}

	public String rename(String oldkey, String newkey) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.rename(oldkey, newkey);
		} finally {
			close(commands);
		}
	}

	public Long renamenx(String oldkey, String newkey) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.renamenx(oldkey, newkey);
		} finally {
			close(commands);
		}
	}

	public String rpoplpush(String srckey, String dstkey) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.rpoplpush(srckey, dstkey);
		} finally {
			close(commands);
		}
	}

	public Set<String> sdiff(String... keys) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.sdiff(keys);
		} finally {
			close(commands);
		}
	}

	public Long sdiffstore(String dstkey, String... keys) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.sdiffstore(dstkey, keys);
		} finally {
			close(commands);
		}
	}

	public Set<String> sinter(String... keys) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.sinter(keys);
		} finally {
			close(commands);
		}
	}

	public Long sinterstore(String dstkey, String... keys) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.sinterstore(dstkey, keys);
		} finally {
			close(commands);
		}
	}

	public Long smove(String srckey, String dstkey, String member) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.smove(srckey, dstkey, member);
		} finally {
			close(commands);
		}
	}

	public Long sort(String key, String dstkey) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.sort(key, dstkey);
		} finally {
			close(commands);
		}
	}

	public Set<String> sunion(String... keys) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.sunion(keys);
		} finally {
			close(commands);
		}
	}

	public Long sunionstore(String dstkey, String... keys) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.sunionstore(dstkey, keys);
		} finally {
			close(commands);
		}
	}

	public Long zinterstore(String dstkey, String... sets) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.zinterstore(dstkey, sets);
		} finally {
			close(commands);
		}
	}

	public Long zunionstore(String dstkey, String... sets) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.zunionstore(dstkey, sets);
		} finally {
			close(commands);
		}
	}

	public String brpoplpush(String source, String destination, int timeout) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.brpoplpush(source, destination, timeout);
		} finally {
			close(commands);
		}
	}

	public Long publish(String channel, String message) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.publish(channel, message);
		} finally {
			close(commands);
		}
	}

	public String pfmerge(String destkey, String... sourcekeys) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.pfmerge(destkey, sourcekeys);
		} finally {
			close(commands);
		}
	}

	public long pfcount(String... keys) {
		MultiKeyJedisClusterCommands commands = null;
		try {
			commands = getResource();
			return commands.pfcount(keys);
		} finally {
			close(commands);
		}
	}

}
