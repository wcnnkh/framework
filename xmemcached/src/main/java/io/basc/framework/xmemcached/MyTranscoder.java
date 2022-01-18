package io.basc.framework.xmemcached;

import io.basc.framework.codec.Codec;
import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;

public class MyTranscoder extends SerializingTranscoder {
	private final Codec<Object, byte[]> codec;

	public MyTranscoder(Codec<Object, byte[]> codec) {
		super();
		this.codec = codec;
	}

	public MyTranscoder(int transcoderMaxDataSize, Codec<Object, byte[]> codec) {
		super(transcoderMaxDataSize);
		this.codec = codec;
	}

	@Override
	protected Object deserialize(byte[] in) {
		try {
			return codec == null ? super.deserialize(in) : codec.decode(in);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected byte[] serialize(Object o) {
		return codec == null ? super.serialize(o) : codec.encode(o);
	}
}
