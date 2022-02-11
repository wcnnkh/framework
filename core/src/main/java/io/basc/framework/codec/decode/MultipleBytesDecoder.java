package io.basc.framework.codec.decode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.MultipleDecoder;

public interface MultipleBytesDecoder extends BytesDecoder, MultipleDecoder<byte[]> {
	default void decode(InputStream source, int bufferSize, OutputStream target) throws DecodeException, IOException {
		decode(source, bufferSize, target, getCount());
	}

	default void decode(InputStream source, int bufferSize, OutputStream target, int count)
			throws DecodeException, IOException {
		byte[] value = decode(source, bufferSize, count);
		target.write(value);
		target.flush();
	}

	@Override
	default byte[] decode(byte[] source, int count) throws DecodeException {
		if (source == null) {
			return null;
		}

		try {
			return decode(new ByteArrayInputStream(source), source.length, count);
		} catch (IOException e) {
			throw new DecodeException(e);
		}
	}

	@Override
	default byte[] decode(byte[] source) throws DecodeException {
		return MultipleDecoder.super.decode(source);
	}

	byte[] decode(InputStream source, int bufferSize, int count) throws IOException, DecodeException;
}
