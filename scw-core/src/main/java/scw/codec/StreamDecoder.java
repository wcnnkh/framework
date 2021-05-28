package scw.codec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.core.Assert;
import scw.io.IOUtils;
import scw.io.UnsafeByteArrayInputStream;
import scw.io.UnsafeByteArrayOutputStream;

public interface StreamDecoder extends MultipleDecoder<byte[]> {
	void decode(InputStream source, OutputStream target) throws IOException, DecodeException;

	default void decode(File source, OutputStream target) throws IOException, DecodeException {
		if (!source.exists()) {
			return;
		}

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(source);
			decode(fis, target);
		} finally {
			IOUtils.close(fis);
		}
	}

	default void decode(File source, File target) throws IOException, DecodeException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(target);
			decode(source, fos);
		} finally {
			IOUtils.close(fos);
		}
	}

	@Override
	default byte[] decode(byte[] source) throws DecodeException {
		UnsafeByteArrayOutputStream target = new UnsafeByteArrayOutputStream();
		UnsafeByteArrayInputStream input = new UnsafeByteArrayInputStream(source);
		try {
			decode(input, target);
		} catch (IOException e) {
			throw new DecodeException(e);
		} finally {
			IOUtils.closeQuietly(input, target);
		}
		return target.toByteArray();
	}

	static class SimpleStreamDecoder implements StreamDecoder {
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
}