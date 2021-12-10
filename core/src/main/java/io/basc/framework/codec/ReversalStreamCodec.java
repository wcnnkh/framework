package io.basc.framework.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

final class ReversalStreamCodec implements StreamCodec {
	private final StreamCodec codec;

	ReversalStreamCodec(StreamCodec codec) {
		this.codec = codec;
	}

	@Override
	public StreamCodec reversal() {
		return codec;
	}

	@Override
	public void encode(InputStream source, OutputStream target) throws IOException, EncodeException {
		try {
			codec.decode(source, target);
		} catch (EncodeException e) {
			throw new DecodeException("reversal", e);
		}
	}

	@Override
	public void decode(InputStream source, OutputStream target) throws IOException, DecodeException {
		try {
			codec.encode(source, target);
		} catch (DecodeException e) {
			throw new EncodeException("reversal", e);
		}
	}
}