package io.basc.framework.codec;

import io.basc.framework.core.Assert;
import io.basc.framework.io.IOUtils;
import io.basc.framework.io.UnsafeByteArrayInputStream;
import io.basc.framework.io.UnsafeByteArrayOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StreamEncoder extends MultipleEncoder<byte[]> {
	void encode(InputStream source, OutputStream target) throws IOException, EncodeException;

	default void encode(File source, OutputStream target) throws IOException, EncodeException {
		if (!source.exists()) {
			return;
		}

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(source);
			encode(fis, target);
		} finally {
			IOUtils.close(fis);
		}
	}

	default void encode(File source, File target) throws IOException, EncodeException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(target);
			encode(source, fos);
		} finally {
			IOUtils.close(fos);
		}
	}

	@Override
	default byte[] encode(byte[] source) throws EncodeException {
		UnsafeByteArrayOutputStream target = new UnsafeByteArrayOutputStream();
		UnsafeByteArrayInputStream input = new UnsafeByteArrayInputStream(source);
		try {
			encode(input, target);
		} catch (IOException e) {
			throw new EncodeException(e);
		} finally {
			IOUtils.closeQuietly(input, target);
		}
		return target.toByteArray();
	}

	static class SimpleStreamEncoder implements StreamEncoder {
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

	static StreamEncoder build(Encoder<byte[], byte[]> encoder) {
		return new SimpleStreamEncoder(encoder);
	}
}
