package scw.data.memcached.x;

import java.io.IOException;

import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;
import scw.io.serialzer.Serializer;
import scw.lang.NestedRuntimeException;

public class MyTranscoder extends SerializingTranscoder {
	private final Serializer serializer;

	public MyTranscoder(Serializer serializer) {
		super();
		this.serializer = serializer;
	}

	public MyTranscoder(int transcoderMaxDataSize, Serializer serializer) {
		super(transcoderMaxDataSize);
		this.serializer = serializer;
	}

	@Override
	protected Object deserialize(byte[] in) {
		try {
			return serializer == null ? super.deserialize(in) : serializer.deserialize(in);
		} catch (Exception e) {
			throw new NestedRuntimeException(e);
		}
	}

	@Override
	protected byte[] serialize(Object o) {
		try {
			return serializer == null ? super.serialize(o) : serializer.serialize(o);
		} catch (IOException e) {
			throw new NestedRuntimeException(e);
		}
	}
}
