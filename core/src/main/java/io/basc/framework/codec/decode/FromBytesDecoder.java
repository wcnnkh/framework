package io.basc.framework.codec.decode;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.Decoder;
import io.basc.framework.io.IOUtils;
import io.basc.framework.io.Resource;

public interface FromBytesDecoder<D> extends Decoder<byte[], D> {
	D decode(InputStream source, int bufferSize) throws IOException, DecodeException;

	default D decode(InputStream source) throws IOException, DecodeException {
		return decode(source, IOUtils.DEFAULT_BUFFER_SIZE);
	}

	@Override
	default D decode(byte[] source) throws DecodeException {
		try {
			return decode(new ByteArrayInputStream(source), source.length);
		} catch (IOException e) {
			// 理论上不会执行到这里,除非解码内部抛出io异常
			throw new DecodeException(e);
		}
	}

	default D decode(ByteBuffer source) throws DecodeException {
		byte[] buf = new byte[source.position()];
		source.get(buf, 0, buf.length);
		return decode(buf);
	}

	@Override
	default <T> FromBytesDecoder<T> toDecoder(Decoder<D, T> decoder) {
		return new NestedFromBytesDecoder<>(this, decoder);
	}

	default D decode(File source) throws IOException, DecodeException {
		return decode(source, IOUtils.DEFAULT_BUFFER_SIZE);
	}

	default D decode(File source, int bufferSize) throws IOException, DecodeException {
		if (!source.exists()) {
			return null;
		}

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(source);
			return decode(fis, bufferSize);
		} finally {
			IOUtils.close(fis);
		}
	}

	default D decode(Resource source) throws IOException, DecodeException {
		return source.read((is) -> decode(is));
	}

	default D decode(Resource source, int bufferSize) throws IOException, DecodeException {
		return source.read((is) -> decode(is, bufferSize));
	}
}
