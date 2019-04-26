package scw.data.redis.jedis;

import java.util.List;
import java.util.Set;

import redis.clients.jedis.MultiKeyCommands;
import scw.data.redis.ResourceManager;

public abstract class AbstractMultikeyCommands
		implements scw.data.redis.MultiKeyCommands, ResourceManager<MultiKeyCommands> {

	public Long del(String... keys) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.del(keys);
		} finally {
			close(resource);
		}
	}

	public Long exists(String... keys) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.exists(keys);
		} finally {
			close(resource);
		}
	}

	public List<String> blpop(int timeout, String... keys) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.blpop(timeout, keys);
		} finally {
			close(resource);
		}
	}

	public List<String> brpop(int timeout, String... keys) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.brpop(timeout, keys);
		} finally {
			close(resource);
		}
	}

	public List<String> blpop(String... args) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.blpop(args);
		} finally {
			close(resource);
		}
	}

	public List<String> brpop(String... args) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.brpop(args);
		} finally {
			close(resource);
		}
	}

	public Set<String> keys(String pattern) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.keys(pattern);
		} finally {
			close(resource);
		}
	}

	public List<String> mget(String... keys) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.mget(keys);
		} finally {
			close(resource);
		}
	}

	public String mset(String... keysvalues) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.mset(keysvalues);
		} finally {
			close(resource);
		}
	}

	public Long msetnx(String... keysvalues) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.msetnx(keysvalues);
		} finally {
			close(resource);
		}
	}

	public String rename(String oldkey, String newkey) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.rename(oldkey, newkey);
		} finally {
			close(resource);
		}
	}

	public Long renamenx(String oldkey, String newkey) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.renamenx(oldkey, newkey);
		} finally {
			close(resource);
		}
	}

	public String rpoplpush(String srckey, String dstkey) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.rpoplpush(srckey, dstkey);
		} finally {
			close(resource);
		}
	}

	public Set<String> sdiff(String... keys) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.sdiff(keys);
		} finally {
			close(resource);
		}
	}

	public Long sdiffstore(String dstkey, String... keys) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.sdiffstore(dstkey, keys);
		} finally {
			close(resource);
		}
	}

	public Set<String> sinter(String... keys) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.sinter(keys);
		} finally {
			close(resource);
		}
	}

	public Long sinterstore(String dstkey, String... keys) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.sinterstore(dstkey, keys);
		} finally {
			close(resource);
		}
	}

	public Long smove(String srckey, String dstkey, String member) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.smove(srckey, dstkey, member);
		} finally {
			close(resource);
		}
	}

	public Long sort(String key, String dstkey) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.sort(key, dstkey);
		} finally {
			close(resource);
		}
	}

	public Set<String> sunion(String... keys) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.sunion(keys);
		} finally {
			close(resource);
		}
	}

	public Long sunionstore(String dstkey, String... keys) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.sunionstore(dstkey, keys);
		} finally {
			close(resource);
		}
	}

	public String watch(String... keys) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.watch(keys);
		} finally {
			close(resource);
		}
	}

	public String unwatch() {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.unwatch();
		} finally {
			close(resource);
		}
	}

	public Long zinterstore(String dstkey, String... sets) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.zinterstore(dstkey, sets);
		} finally {
			close(resource);
		}
	}

	public Long zunionstore(String dstkey, String... sets) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.zunionstore(dstkey, sets);
		} finally {
			close(resource);
		}
	}

	public String brpoplpush(String source, String destination, int timeout) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.brpoplpush(source, destination, timeout);
		} finally {
			close(resource);
		}
	}

	public Long publish(String channel, String message) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.publish(channel, message);
		} finally {
			close(resource);
		}
	}

	public String randomKey() {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.randomKey();
		} finally {
			close(resource);
		}
	}

	public String pfmerge(String destkey, String... sourcekeys) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.pfmerge(destkey, sourcekeys);
		} finally {
			close(resource);
		}
	}

	public long pfcount(String... keys) {
		MultiKeyCommands resource = null;
		try {
			resource = getResource();
			return resource.pfcount(keys);
		} finally {
			close(resource);
		}
	}

}
