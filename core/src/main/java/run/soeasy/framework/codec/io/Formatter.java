package run.soeasy.framework.codec.io;

import run.soeasy.framework.codec.Codec;

public interface Formatter<T> extends Codec<T, String> {
	@Override
	CharBufferDecoder<T> decoder();

	@Override
	CharBufferEncoder<T> encoder();
}
