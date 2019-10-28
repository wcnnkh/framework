package scw.data.redis.operations;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import scw.core.exception.NotSupportException;
import scw.data.redis.RedisOperations;

public abstract class AbstractBinaryRedisOperations implements RedisOperations<byte[], byte[]> {

	public long incr(byte[] key, long incr, long initValue) {
		throw new NotSupportException("不支持此操作");
	}

	public long decr(byte[] key, long decr, long initValue) {
		throw new NotSupportException("不支持此操作");
	}

	public byte[] getAndTouch(byte[] key, int newExp) {
		byte[] v = get(key);
		if (v != null) {
			expire(key, newExp);
		}
		return v;
	}
	
	public Map<byte[], byte[]> mget(Collection<byte[]> keys) {
		if (keys == null || keys.isEmpty()) {
			return null;
		}

		List<byte[]> list = mget(keys.toArray(new byte[keys.size()][]));
		if (list == null || list.isEmpty()) {
			return null;
		}

		Map<byte[], byte[]> map = new HashMap<byte[], byte[]>(keys.size(), 1);
		Iterator<byte[]> keyIterator = keys.iterator();
		Iterator<byte[]> valueIterator = list.iterator();
		while (keyIterator.hasNext() && valueIterator.hasNext()) {
			byte[] key = keyIterator.next();
			byte[] value = valueIterator.next();
			if (key == null || value == null) {
				continue;
			}

			map.put(key, value);
		}
		return map;
	}
}
