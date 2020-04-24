package scw.data.memcached.x;

import java.io.IOException;

import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;
import scw.core.parameter.annotation.ParameterName;
import scw.io.Serializer;
import scw.lang.NestedRuntimeException;

public class MyTranscoder extends SerializingTranscoder {
	private static final String SERIALIZER_NAME = "memcached.serializer";

	private final Serializer serializer;

	public MyTranscoder(@ParameterName(SERIALIZER_NAME) Serializer serializer) {
		super();
		this.serializer = serializer;
	}

	public MyTranscoder(@ParameterName("memcached.transcoder.max-data-size") int transcoderMaxDataSize,
			@ParameterName(SERIALIZER_NAME) Serializer serializer) {
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
