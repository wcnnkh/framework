package io.basc.framework.codec.decode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.MultipleDecoder;
import io.basc.framework.io.BufferProcessor;
import io.basc.framework.io.FileUtils;
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

	default <E extends Throwable> void decode(File source, int bufferSize, BufferProcessor<byte[], E> targetProcessor)
			throws IOException, DecodeException, E {
		FileInputStream fis = new FileInputStream(source);
		try {
			decode(fis, bufferSize, targetProcessor);
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
			decode(fis, bufferSize, target);
		} finally {
			IOUtils.close(fis);
		}
	}

	default void decode(File source, int bufferSize, OutputStream target, int count)
			throws DecodeException, IOException {
		FileInputStream fis = new FileInputStream(source);
		try {
			decode(fis, bufferSize, target, count);
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

	/**
	 * 默认是使用临时文件实现的，如果有更好的实现应该重写此方法
	 * 
	 * @param source
	 * @param bufferSize
	 * @param target
	 * @param count
	 * @throws DecodeException
	 * @throws IOException
	 */
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

	/**
	 * 默认是使用临时文件实现的，如果有更好的实现应该重写此方法
	 * 
	 * @param source
	 * @param bufferSize
	 * @param targetProcessor
	 * @param count
	 * @throws DecodeException
	 * @throws IOException
	 * @throws E
	 */
	default <E extends Throwable> void decode(InputStream source, int bufferSize,
			BufferProcessor<byte[], E> targetProcessor, int count) throws DecodeException, IOException, E {
		Assert.isTrue(count > 0, "Count must be greater than 0");
		if (count == 1) {
			decode(source, bufferSize, targetProcessor);
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
			decode(lastFile, bufferSize, targetProcessor);
		} finally {
			lastFile.delete();
		}
	}

	/**
	 * 默认是使用临时文件实现的，如果有更好的实现应该重写此方法
	 * 
	 * @param source
	 * @param bufferSize
	 * @param targetProcessor
	 * @throws DecodeException
	 * @throws IOException
	 * @throws E
	 */
	default <E extends Throwable> void decode(InputStream source, int bufferSize,
			BufferProcessor<byte[], E> targetProcessor) throws DecodeException, IOException, E {
		File tempFile = File.createTempFile("decode", "processor");
		try {
			decode(source, bufferSize, tempFile);
			FileUtils.read(tempFile, bufferSize, targetProcessor);
		} finally {
			tempFile.delete();
		}
	}
}
