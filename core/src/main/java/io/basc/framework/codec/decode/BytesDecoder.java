package io.basc.framework.codec.decode;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.Decoder;
import io.basc.framework.io.IOUtils;

public interface BytesDecoder<D> extends Decoder<byte[], D> {
	@Override
	default D decode(byte[] source) throws DecodeException {
		try {
			return decode(new ByteArrayInputStream(source));
		} catch (Exception e) {
			// 理论上不会执行到这里,除非解码内部抛出io异常
			throw new DecodeException(e);
		}
	}

	D decode(InputStream source) throws IOException, DecodeException;

	default D decode(File source) throws IOException, DecodeException {
		if (!source.exists()) {
			return null;
		}

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(source);
			return decode(fis);
		} finally {
			IOUtils.close(fis);
		}
	}
}
