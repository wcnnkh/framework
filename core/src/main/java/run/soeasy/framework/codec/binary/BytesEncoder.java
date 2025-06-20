package run.soeasy.framework.codec.binary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.codec.MultipleEncoder;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.FileUtils;
import run.soeasy.framework.io.IOUtils;

public interface BytesEncoder extends FromBytesEncoder<byte[]>, ToBytesEncoder<byte[]>, MultipleEncoder<byte[]> {

	@Override
	default byte[] encode(byte[] source) throws EncodeException {
		return FromBytesEncoder.super.encode(source);
	}

	default void encode(byte[] source, File target, int count) throws IOException, EncodeException {
		ByteArrayInputStream input = new ByteArrayInputStream(source);
		encode(input, source.length, target, count);
	}

	@Override
	default void encode(byte[] source, OutputStream target) throws IOException, EncodeException {
		ByteArrayInputStream input = new ByteArrayInputStream(source);
		encode(input, source.length, target);
	}

	default void encode(byte[] source, OutputStream target, int count) throws IOException, EncodeException {
		ByteArrayInputStream input = new ByteArrayInputStream(source);
		encode(input, source.length, target, count);
	}

	default byte[] encode(InputStream source, int bufferSize, int count) throws IOException, EncodeException {
		Assert.isTrue(count > 0, "Count must be greater than 0");
		byte[] v = encode(source, bufferSize);
		if (count > 1) {
			return encode(v, count - 1);
		}
		return v;
	}

	/**
	 * @see #encode(InputStream, int, OutputStream)
	 */
	@Override
	default byte[] encode(InputStream source, int bufferSize) throws IOException, EncodeException {
		ByteArrayOutputStream target = new ByteArrayOutputStream(bufferSize);
		encode(source, bufferSize, target);
		return target.toByteArray();
	}

	default void encode(InputStream source, OutputStream target) throws IOException, EncodeException {
		encode(source, IOUtils.DEFAULT_BYTE_BUFFER_SIZE, target);
	}

	default void encode(InputStream source, int bufferSize, OutputStream target, int count)
			throws IOException, EncodeException {
		Assert.isTrue(count > 0, "Count must be greater than 0");
		if (count == 1) {
			encode(source, bufferSize, target);
			return;
		}

		// 前n-1次都使用临时文件存储
		// 后一次的结果依赖前一次
		File firstFile = File.createTempFile("encode", "count.0");
		encode(source, bufferSize, firstFile);

		File lastFile = firstFile;
		for (int i = 1; i < count - 1; i++) {
			File targetFile = File.createTempFile("encode", "count." + i);
			try {
				encode(lastFile, bufferSize, targetFile);
			} finally {
				// 删除上一次的临时文件
				lastFile.delete();
			}
			lastFile = targetFile;
		}

		try {
			encode(lastFile, bufferSize, target);
		} finally {
			lastFile.delete();
		}
	}

	default void encode(File source, int bufferSize, OutputStream target) throws IOException, EncodeException {
		FileInputStream fis = new FileInputStream(source);
		try {
			encode(fis, bufferSize, target);
		} finally {
			fis.close();
		}
	}

	default void encode(File source, int bufferSize, OutputStream target, int count)
			throws IOException, EncodeException {
		FileInputStream fis = new FileInputStream(source);
		try {
			encode(fis, bufferSize, target, count);
		} finally {
			fis.close();
		}
	}

	default void encode(InputStream source, int bufferSize, File target) throws IOException, EncodeException {
		FileOutputStream fos = new FileOutputStream(target);
		try {
			encode(source, bufferSize, fos);
		} finally {
			fos.close();
		}
	}

	default void encode(InputStream source, int bufferSize, File target, int count)
			throws IOException, EncodeException {
		FileOutputStream fos = new FileOutputStream(target);
		try {
			encode(source, bufferSize, fos, count);
		} finally {
			fos.close();
		}
	}

	default void encode(File source, int bufferSize, File target) throws IOException, EncodeException {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(target);
			encode(fis, bufferSize, fos);
		} finally {
			IOUtils.close(fis, fos);
		}
	}

	default void encode(File source, int bufferSize, File target, int count) throws IOException, EncodeException {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			fis = new FileInputStream(source);
			fos = new FileOutputStream(target);
			encode(fis, bufferSize, fos, count);
		} finally {
			IOUtils.close(fis, fos);
		}
	}

	void encode(InputStream source, int bufferSize, OutputStream target) throws IOException, EncodeException;

	default <E extends Throwable> void encode(InputStream source, int bufferSize,
			BufferConsumer<? super byte[], ? extends E> targetConsumer, int count)
			throws IOException, EncodeException, E {
		Assert.isTrue(count > 0, "Count must be greater than 0");
		if (count == 1) {
			encode(source, bufferSize, targetConsumer);
			return;
		}

		// 前n-1次都使用临时文件存储
		// 后一次的结果依赖前一次
		File firstFile = File.createTempFile("encode", "count.0");
		encode(source, bufferSize, firstFile);

		File lastFile = firstFile;
		for (int i = 1; i < count - 1; i++) {
			File targetFile = File.createTempFile("encode", "count." + i);
			try {
				encode(lastFile, bufferSize, targetFile);
			} finally {
				// 删除上一次的临时文件
				lastFile.delete();
			}
			lastFile = targetFile;
		}

		try {
			encode(lastFile, bufferSize, targetConsumer);
		} finally {
			lastFile.delete();
		}
	}

	default <E extends Throwable> void encode(File source, int bufferSize,
			BufferConsumer<? super byte[], ? extends E> targetConsumer) throws IOException, EncodeException, E {
		FileInputStream fis = new FileInputStream(source);
		try {
			encode(fis, bufferSize, targetConsumer);
		} finally {
			fis.close();
		}
	}

	default <E extends Throwable> void encode(InputStream source, int bufferSize,
			BufferConsumer<? super byte[], ? extends E> targetConsumer) throws IOException, EncodeException, E {
		File tempFile = File.createTempFile("encode", "processor");
		try {
			encode(source, bufferSize, tempFile);
			FileUtils.copy(tempFile, bufferSize, targetConsumer);
		} finally {
			tempFile.delete();
		}
	}
}
