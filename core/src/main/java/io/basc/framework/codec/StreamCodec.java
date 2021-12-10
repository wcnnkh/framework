package io.basc.framework.codec;

public interface StreamCodec extends StreamEncoder, StreamDecoder, MultipleCodec<byte[]> {
	default StreamCodec reversal() {
		return new ReversalStreamCodec(this);
	}
}
