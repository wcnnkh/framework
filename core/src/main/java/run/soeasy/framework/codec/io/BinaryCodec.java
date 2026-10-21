package run.soeasy.framework.codec.io;

import run.soeasy.framework.codec.UnaryCodec;
import run.soeasy.framework.codec.commons.ToBinaryCodec;

public interface BinaryCodec extends UnaryCodec<byte[]>, ToBinaryCodec<byte[]> {
	@Override
	BinaryCoder encoder();

	@Override
	BinaryCoder decoder();
}
