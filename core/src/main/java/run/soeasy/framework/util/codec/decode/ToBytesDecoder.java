package run.soeasy.framework.util.codec.decode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import run.soeasy.framework.util.codec.DecodeException;
import run.soeasy.framework.util.codec.Decoder;
import run.soeasy.framework.util.io.BufferProcessor;
import run.soeasy.framework.util.io.FileUtils;
import run.soeasy.framework.util.io.UnsafeByteArrayOutputStream;

public interface ToBytesDecoder<E> extends Decoder<E, byte[]> {

	@Override
	default byte[] decode(E source) throws DecodeException {
		UnsafeByteArrayOutputStream target = new UnsafeByteArrayOutputStream();
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
	 * @param <S>             异常类型
	 * @param source          输入
	 * @param targetProcessor 输出
	 * @throws DecodeException 解码异常
	 * @throws IOException     io error
	 * @throws S               异常类型
	 */
	default <S extends Throwable> void decode(E source, BufferProcessor<byte[], S> targetProcessor)
			throws DecodeException, IOException, S {
		File tempFile = File.createTempFile("decode", "processor");
		try {
			decode(source, tempFile);
			FileUtils.read(tempFile, targetProcessor);
		} finally {
			tempFile.delete();
		}
	}
}
