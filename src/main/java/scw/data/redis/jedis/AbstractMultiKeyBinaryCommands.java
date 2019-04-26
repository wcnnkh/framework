package scw.data.redis.jedis;

import java.util.List;
import java.util.Set;

import redis.clients.jedis.MultiKeyBinaryCommands;
import scw.data.redis.ResourceManager;

public abstract class AbstractMultiKeyBinaryCommands
		implements scw.data.redis.MultiKeyBinaryCommands, ResourceManager<MultiKeyBinaryCommands> {

	public Long del(byte[]... keys) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.del(keys);
		} finally {
			close(commands);
		}
	}

	public Long exists(byte[]... keys) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.exists(keys);
		} finally {
			close(commands);
		}
	}

	public List<byte[]> blpop(int timeout, byte[]... keys) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.blpop(timeout, keys);
		} finally {
			close(commands);
		}
	}

	public List<byte[]> brpop(int timeout, byte[]... keys) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.brpop(timeout, keys);
		} finally {
			close(commands);
		}
	}

	public List<byte[]> blpop(byte[]... args) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.blpop(args);
		} finally {
			close(commands);
		}
	}

	public List<byte[]> brpop(byte[]... args) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.brpop(args);
		} finally {
			close(commands);
		}
	}

	public Set<byte[]> keys(byte[] pattern) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.keys(pattern);
		} finally {
			close(commands);
		}
	}

	public List<byte[]> mget(byte[]... keys) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.mget(keys);
		} finally {
			close(commands);
		}
	}

	public String mset(byte[]... keysvalues) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.mset(keysvalues);
		} finally {
			close(commands);
		}
	}

	public Long msetnx(byte[]... keysvalues) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.msetnx(keysvalues);
		} finally {
			close(commands);
		}
	}

	public String rename(byte[] oldkey, byte[] newkey) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.rename(oldkey, newkey);
		} finally {
			close(commands);
		}
	}

	public Long renamenx(byte[] oldkey, byte[] newkey) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.renamenx(oldkey, newkey);
		} finally {
			close(commands);
		}
	}

	public byte[] rpoplpush(byte[] srckey, byte[] dstkey) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.rpoplpush(srckey, dstkey);
		} finally {
			close(commands);
		}
	}

	public Set<byte[]> sdiff(byte[]... keys) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.sdiff(keys);
		} finally {
			close(commands);
		}
	}

	public Long sdiffstore(byte[] dstkey, byte[]... keys) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.sdiffstore(dstkey, keys);
		} finally {
			close(commands);
		}
	}

	public Set<byte[]> sinter(byte[]... keys) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.sinter(keys);
		} finally {
			close(commands);
		}
	}

	public Long sinterstore(byte[] dstkey, byte[]... keys) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.sinterstore(dstkey, keys);
		} finally {
			close(commands);
		}
	}

	public Long smove(byte[] srckey, byte[] dstkey, byte[] member) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.smove(srckey, dstkey, member);
		} finally {
			close(commands);
		}
	}

	public Long sort(byte[] key, byte[] dstkey) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.sort(key, dstkey);
		} finally {
			close(commands);
		}
	}

	public Set<byte[]> sunion(byte[]... keys) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.sunion(keys);
		} finally {
			close(commands);
		}
	}

	public Long sunionstore(byte[] dstkey, byte[]... keys) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.sunionstore(dstkey, keys);
		} finally {
			close(commands);
		}
	}

	public String watch(byte[]... keys) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.watch(keys);
		} finally {
			close(commands);
		}
	}

	public String unwatch() {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.unwatch();
		} finally {
			close(commands);
		}
	}

	public Long zinterstore(byte[] dstkey, byte[]... sets) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.zinterstore(dstkey, sets);
		} finally {
			close(commands);
		}
	}

	public Long zunionstore(byte[] dstkey, byte[]... sets) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.zunionstore(dstkey, sets);
		} finally {
			close(commands);
		}
	}

	public byte[] brpoplpush(byte[] source, byte[] destination, int timeout) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.brpoplpush(source, destination, timeout);
		} finally {
			close(commands);
		}
	}

	public Long publish(byte[] channel, byte[] message) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.publish(channel, message);
		} finally {
			close(commands);
		}
	}

	public byte[] randomBinaryKey() {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.randomBinaryKey();
		} finally {
			close(commands);
		}
	}

	public String pfmerge(byte[] destkey, byte[]... sourcekeys) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.pfmerge(destkey, sourcekeys);
		} finally {
			close(commands);
		}
	}

	public Long pfcount(byte[]... keys) {
		MultiKeyBinaryCommands commands = null;
		try {
			commands = getResource();
			return commands.pfcount(keys);
		} finally {
			close(commands);
		}
	}

}
