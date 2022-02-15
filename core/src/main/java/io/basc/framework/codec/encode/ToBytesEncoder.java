package io.basc.framework.codec.encode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import io.basc.framework.codec.EncodeException;
import io.basc.framework.codec.Encoder;
import io.basc.framework.codec.support.Base64;
import io.basc.framework.codec.support.HexCodec;
import io.basc.framework.io.IOUtils;
import io.basc.framework.io.UnsafeByteArrayOutputStream;

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
}
