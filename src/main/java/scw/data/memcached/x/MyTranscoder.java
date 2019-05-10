package scw.data.memcached.x;

import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;
import scw.core.serializer.Serializer;

public final class MyTranscoder extends SerializingTranscoder {
	private final Serializer serializer;

	public MyTranscoder(Serializer serializer) {
		super();
		this.serializer = serializer;
	}

	public MyTranscoder(int max, Serializer serializer) {
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
