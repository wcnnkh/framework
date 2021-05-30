package scw.redis.jedis;

import redis.clients.jedis.util.SafeEncoder;
import scw.codec.Codec;
import scw.codec.DecodeException;
import scw.codec.EncodeException;

public class JedisCodec implements Codec<String, byte[]>{

	@Override
	public byte[] encode(String source) throws EncodeException {
		return SafeEncoder.encode(source);
	}

	@Override
	public String decode(byte[] source) throws DecodeException {
		return SafeEncoder.encode(source);
	}

}
