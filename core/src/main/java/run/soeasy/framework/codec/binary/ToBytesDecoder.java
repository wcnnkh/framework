package run.soeasy.framework.codec.binary;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.Decoder;
import run.soeasy.framework.io.BufferConsumer;
import run.soeasy.framework.io.FileUtils;

public interface ToBytesDecoder<E> extends Decoder<E, byte[]> {

	@Override
	default byte[] decode(E source) throws DecodeException {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try {
			decode(source, target);
		} catch (IOException e) {
			throw new DecodeException(e);
		}
		return target.toByteArray();
	}

	default void decode(E source, File target) throws DecodeException, IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(target);
			decode(source, fos);
		} finally {
			fos.close();
		}
	}

	void decode(E source, OutputStream target) throws DecodeException, IOException;

	/**
	 * 默认是使用临时文件实现的，如果有更好的实现应该重写此方法
	 * 
	 * @param <S>            异常类型
	 * @param source         输入
	 * @param targetConsumer 输出
	 * @throws DecodeException 解码异常
	 * @throws IOException     io error
	 * @throws S               异常类型
	 */
	default <S extends Throwable> void decode(E source, BufferConsumer<? super byte[], ? extends S> targetConsumer)
			throws DecodeException, IOException, S {
		File tempFile = File.createTempFile("decode", "processor");
		try {
			decode(source, tempFile);
			FileUtils.copy(tempFile, targetConsumer);
		} finally {
			tempFile.delete();
		}
	}
}
