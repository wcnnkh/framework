package run.soeasy.framework.codec.io;

import run.soeasy.framework.codec.UnaryCodec;

public interface BinaryCodec extends UnaryCodec<byte[]>, Serializer<byte[]> {
	@Override
	BinaryEncoder encoder();

	@Override
	BinaryDecoder decoder();
}
