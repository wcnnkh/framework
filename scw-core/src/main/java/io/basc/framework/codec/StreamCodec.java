package io.basc.framework.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StreamCodec extends StreamEncoder, StreamDecoder, MultipleCodec<byte[]> {
	default StreamCodec reversal() {
		return new ReversalStreamCodec(this);
	}

	static class ReversalStreamCodec implements StreamCodec {
		private final StreamCodec codec;

		public ReversalStreamCodec(StreamCodec codec) {
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

	static StreamCodec build(StreamEncoder encoder, StreamDecoder decoder) {
		return new StreamCodec() {

			@Override
			public void decode(InputStream source, OutputStream target) throws IOException, DecodeException {
				decoder.decode(source, target);
			}

			@Override
			public void encode(InputStream source, OutputStream target) throws IOException, EncodeException {
				encoder.encode(source, target);
			}
		};
	}

	static StreamCodec build(Codec<byte[], byte[]> codec) {
		return build(StreamEncoder.build(codec), StreamDecoder.build(codec));
	}
}
