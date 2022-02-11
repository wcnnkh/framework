package io.basc.framework.codec;

import io.basc.framework.codec.support.BytesCodec;

public interface StreamCodec extends StreamEncoder, StreamDecoder, MultipleCodec<byte[]>, BytesCodec<byte[]> {
	default StreamCodec reversal() {
		return new ReversalStreamCodec(this);
	}
}
