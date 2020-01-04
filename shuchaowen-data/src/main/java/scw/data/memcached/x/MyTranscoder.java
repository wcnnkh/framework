package scw.data.memcached.x;

import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;
import scw.core.annotation.ParameterName;
import scw.serializer.Serializer;

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
		return serializer == null ? super.deserialize(in) : serializer.deserialize(in);
	}

	@Override
	protected byte[] serialize(Object o) {
		return serializer == null ? super.serialize(o) : serializer.serialize(o);
	}
}
