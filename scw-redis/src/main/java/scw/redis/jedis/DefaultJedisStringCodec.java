package scw.redis.jedis;

import redis.clients.jedis.util.SafeEncoder;
import scw.codec.Codec;

public final class DefaultJedisStringCodec implements Codec<String, byte[]> {

	public byte[] encode(String text) {
		return SafeEncoder.encode(text);
	}

	public String decode(byte[] bytes) {
		return SafeEncoder.encode(bytes);
	}

}
