package io.basc.framework.codec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.io.IOUtils;
import io.basc.framework.util.Assert;

public final class StreamCodecUtils {
	private StreamCodecUtils() {
	}

	private static class SimpleStreamEncoder implements StreamEncoder {
		private final Encoder<byte[], byte[]> encoder;

		public SimpleStreamEncoder(Encoder<byte[], byte[]> encoder) {
			this.encoder = encoder;
		}

		@Override
		public void encode(InputStream source, OutputStream target) throws IOException, EncodeException {
			Assert.requiredArgument(source != null, "source");
			Assert.requiredArgument(target != null, "target");
			byte[] encode = IOUtils.toByteArray(source);
			encode = encoder.encode(encode);
			target.write(encode);
		}
	}

	public static StreamEncoder build(Encoder<byte[], byte[]> encoder) {
		return new SimpleStreamEncoder(encoder);
	}

	private static class SimpleStreamDecoder implements StreamDecoder {
		private final Decoder<byte[], byte[]> decoder;

		public SimpleStreamDecoder(Decoder<byte[], byte[]> decoder) {
			this.decoder = decoder;
		}

		@Override
		public void decode(InputStream source, OutputStream target) throws IOException, DecodeException {
			Assert.requiredArgument(source != null, "source");
			Assert.requiredArgument(target != null, "target");
			byte[] decode = IOUtils.toByteArray(source);
			decode = decoder.decode(decode);
			target.write(decode);
		}
	}

	static StreamDecoder build(Decoder<byte[], byte[]> decoder) {
		return new SimpleStreamDecoder(decoder);
	}

	public static StreamCodec build(StreamEncoder encoder, StreamDecoder decoder) {
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

	public static StreamCodec build(Codec<byte[], byte[]> codec) {
		return build(build(codec), build(codec));
	}
}
