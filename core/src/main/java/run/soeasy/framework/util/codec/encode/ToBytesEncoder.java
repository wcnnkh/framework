package run.soeasy.framework.util.codec.encode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import run.soeasy.framework.util.codec.EncodeException;
import run.soeasy.framework.util.codec.Encoder;
import run.soeasy.framework.util.codec.support.Base64;
import run.soeasy.framework.util.codec.support.HexCodec;
import run.soeasy.framework.util.io.BufferProcessor;
import run.soeasy.framework.util.io.FileUtils;
import run.soeasy.framework.util.io.IOUtils;
import run.soeasy.framework.util.io.UnsafeByteArrayOutputStream;

public interface ToBytesEncoder<D> extends Encoder<D, byte[]> {

	default Encoder<D, String> toBase64() {
		return toEncoder(Base64.DEFAULT);
	}

	default Encoder<D, String> toHex() {
		return toEncoder(HexCodec.DEFAULT);
	}

	/**
	 * 会直接将结果转换为16进制字符串
	 * 
	 * @see MD5#DEFAULT
	 * @return
	 */
	default Encoder<D, String> toMD5() {
		return toEncoder(MD5.DEFAULT);
	}

	default Encoder<D, String> toSHA1() {
		return toEncoder(SHA1.DEFAULT);
	}

	@Override
	default byte[] encode(D source) throws EncodeException {
		UnsafeByteArrayOutputStream target = new UnsafeByteArrayOutputStream();
		try {
			encode(source, target);
		} catch (IOException e) {
			throw new EncodeException(e);
		}
		return target.toByteArray();
	}

	default void encode(D source, File target) throws IOException, EncodeException {
		FileOutputStream fos = new FileOutputStream(target);
		try {
			encode(source, fos);
		} finally {
			IOUtils.close(fos);
		}
	}

	void encode(D source, OutputStream target) throws IOException, EncodeException;

	/**
	 * 默认是使用临时文件实现的，如果有更好的实现应该重写此方法
	 * 
	 * @param source
	 * @param targetProcessor
	 * @throws IOException
	 * @throws EncodeException
	 * @throws E
	 */
	default <E extends Throwable> void encode(D source, BufferProcessor<byte[], E> targetProcessor)
			throws IOException, EncodeException, E {
		File tempFile = File.createTempFile("encode", "processor");
		try {
			encode(source, tempFile);
			FileUtils.read(tempFile, targetProcessor);
		} finally {
			tempFile.delete();
		}
	}
}
