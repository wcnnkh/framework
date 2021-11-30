package io.basc.framework.codec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.io.IOUtils;
import io.basc.framework.io.UnsafeByteArrayInputStream;
import io.basc.framework.io.UnsafeByteArrayOutputStream;

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
}
