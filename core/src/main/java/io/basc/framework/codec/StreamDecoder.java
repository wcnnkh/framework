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
}