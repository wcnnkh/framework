package scw.data.redis;

import scw.core.exception.NotSupportException;

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
}
