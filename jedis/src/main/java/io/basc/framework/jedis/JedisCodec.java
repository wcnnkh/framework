package io.basc.framework.jedis;

import io.basc.framework.util.codec.Codec;
import io.basc.framework.util.codec.DecodeException;
import io.basc.framework.util.codec.EncodeException;
import redis.clients.jedis.util.SafeEncoder;

public class JedisCodec implements Codec<String, byte[]> {
	public static final JedisCodec INSTANCE = new JedisCodec();

	@Override
	public byte[] encode(String source) throws EncodeException {
		return SafeEncoder.encode(source);
	}

	@Override
	public String decode(byte[] source) throws DecodeException {
		return SafeEncoder.encode(source);
	}

}
