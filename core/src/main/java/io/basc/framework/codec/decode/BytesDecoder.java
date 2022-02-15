package io.basc.framework.codec.decode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.MultipleDecoder;
import io.basc.framework.io.IOUtils;
import io.basc.framework.io.UnsafeByteArrayInputStream;
import io.basc.framework.io.UnsafeByteArrayOutputStream;
import io.basc.framework.util.Assert;

public interface BytesDecoder extends FromBytesDecoder<byte[]>, ToBytesDecoder<byte[]>, MultipleDecoder<byte[]> {

	default BytesDecoder toDecoder(BytesDecoder decoder) {
		return new NestedBytesDecoder(this, decoder);
	}

	default BytesDecoder fromDecoder(BytesDecoder decoder) {
		return new NestedBytesDecoder(decoder, this);
	}

	@Override
	default byte[] decode(byte[] source) throws DecodeException {
		return FromBytesDecoder.super.decode(source);
	}

	default void decode(byte[] source, File target, int count) throws DecodeException, IOException {
		UnsafeByteArrayInputStream input = new UnsafeByteArrayInputStream(source);
		decode(input, source.length, target, count);
	}

	@Override
	default void decode(byte[] source, OutputStream target) throws DecodeException, IOException {
		UnsafeByteArrayInputStream input = new UnsafeByteArrayInputStream(source);
		decode(input, source.length, target);
	}

	default void decode(byte[] source, OutputStream target, int count) throws DecodeException, IOException {
		UnsafeByteArrayInputStream input = new UnsafeByteArrayInputStream(source);
		decode(input, source.length, target, count);
	}

	@Override
	default byte[] decode(InputStream source, int bufferSize) throws IOException, DecodeException {
		UnsafeByteArrayOutputStream target = new UnsafeByteArrayOutputStream();
		decode(source, bufferSize, target);
		return target.toByteArray();
	}

	@Override
	default byte[] decode(File source, int bufferSize) throws IOException, DecodeException {
		FileInputStream fis = new FileInputStream(source);
		try {
			return decode(fis, bufferSize);
		} finally {
			fis.close();
		}
	}

	default byte[] decode(InputStream source, int bufferSize, int count) throws IOException, DecodeException {
		byte[] v = decode(source, bufferSize);
		if (count > 1) {
			return decode(v, count - 1);
		}
		return v;
	}

	default byte[] decode(File source, int bufferSize, int count) throws IOException, DecodeException {
		FileInputStream fis = new FileInputStream(source);
		try {
			return decode(fis, bufferSize, count);
		} finally {
			fis.close();
		}
	}

	default void decode(InputStream source, OutputStream target) throws DecodeException, IOException {
		decode(source, IOUtils.DEFAULT_BUFFER_SIZE, target);
	}

	default void decode(InputStream source, OutputStream target, int count) throws DecodeException, IOException {
		decode(source, IOUtils.DEFAULT_BUFFER_SIZE, target, count);
	}

	default void decode(File source, int bufferSize, OutputStream target) throws DecodeException, IOException {
		FileInputStream fis = new FileInputStream(source);
		try {
			decode(source, bufferSize, target);
		} finally {
			IOUtils.close(fis);
		}
	}

	default void decode(File source, int bufferSize, OutputStream target, int count)
			throws DecodeException, IOException {
		FileInputStream fis = new FileInputStream(source);
		try {
			decode(source, bufferSize, target, count);
		} finally {
			IOUtils.close(fis);
		}
	}

	default void decode(File source, int bufferSize, File target) throws DecodeException, IOException {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			decode(fis, bufferSize, fos);
		} finally {
			IOUtils.close(fis, fos);
		}
	}

	default void decode(File source, int bufferSize, File target, int count) throws DecodeException, IOException {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			decode(fis, bufferSize, fos, count);
		} finally {
			IOUtils.close(fis, fos);
		}
	}

	default void decode(InputStream source, int bufferSize, File target) throws DecodeException, IOException {
		FileOutputStream fos = new FileOutputStream(target);
		try {
			decode(source, bufferSize, fos);
		} finally {
			IOUtils.close(fos);
		}
	}

	default void decode(InputStream source, int bufferSize, File target, int count)
			throws DecodeException, IOException {
		FileOutputStream fos = new FileOutputStream(target);
		try {
			decode(source, bufferSize, fos, count);
		} finally {
			IOUtils.close(fos);
		}
	}

	default void decode(InputStream source, int bufferSize, OutputStream target, int count)
			throws DecodeException, IOException {
		Assert.isTrue(count > 0, "Count must be greater than 0");
		if (count == 1) {
			decode(source, bufferSize, target);
			return;
		}

		// 前n-1次都使用临时文件存储
		// 后一次的结果依赖前一次
		File firstFile = File.createTempFile("encode", "count.0");
		decode(source, bufferSize, firstFile);

		File lastFile = firstFile;
		for (int i = 1; i < count - 1; i++) {
			File targetFile = File.createTempFile("encode", "count." + i);
			try {
				decode(lastFile, bufferSize, targetFile);
			} finally {
				// 删除上一次的临时文件
				lastFile.delete();
			}
			lastFile = targetFile;
		}

		try {
			decode(lastFile, bufferSize, target);
		} finally {
			lastFile.delete();
		}
	}

	void decode(InputStream source, int bufferSize, OutputStream target) throws DecodeException, IOException;
}
