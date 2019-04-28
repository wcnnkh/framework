package scw.data.redis;

import java.util.Arrays;

import scw.core.Constants;
import scw.core.io.Bytes;

public abstract class AbstractBinaryRedisOperations implements RedisOperations<byte[], byte[]> {

	public long incr(byte[] key, long incr, long initValue) {
		return Long.parseLong((String) eval(INCR_AND_INIT_SCRIPT.getBytes(Constants.DEFAULT_CHARSET),
				Arrays.asList(key), Arrays.asList(Bytes.long2bytes(incr), Bytes.long2bytes(initValue))));
	}

	public long decr(byte[] key, long decr, long initValue) {
		return Long.parseLong((String) eval(DECR_AND_INIT_SCRIPT.getBytes(Constants.DEFAULT_CHARSET),
				Arrays.asList(key), Arrays.asList(Bytes.long2bytes(decr), Bytes.long2bytes(initValue))));
	}

	public byte[] getAndTouch(byte[] key, int newExp) {
		byte[] v = get(key);
		if (v != null) {
			expire(key, newExp);
		}
		return v;
	}
}
