package scw.redis.jedis;

import redis.clients.jedis.util.SafeEncoder;
import scw.codec.AbstractCodec;

public final class DefaultJedisStringCodec extends AbstractCodec<String, byte[]> {

	public byte[] encode(String text) {
		return SafeEncoder.encode(text);
	}

	public String decode(byte[] bytes) {
		return SafeEncoder.encode(bytes);
	}

}
