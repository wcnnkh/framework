package io.basc.framework.codec.encode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.codec.MultipleEncoder;
import io.basc.framework.io.IOUtils;

public interface BytesEncoder extends FromBytesEncoder<byte[]>, ToBytesEncoder<byte[]>, MultipleEncoder<byte[]> {

	@Override
	default byte[] encode(byte[] source) throws EncodeException {
		return MultipleEncoder.super.encode(source);
	}

	@Override
	default byte[] encode(InputStream source, int bufferSize) throws IOException, DecodeException {
		return encode(source, bufferSize, getCount());
	}

	@Override
	default byte[] encode(byte[] encode, int count) throws EncodeException {
		if (encode == null) {
			return null;
		}

		try {
			return encode(new ByteArrayInputStream(encode), encode.length, count);
		} catch (IOException e) {
			throw new EncodeException(e);
		}
	}

	default void encode(InputStream source, OutputStream target) throws IOException, EncodeException {
		encode(source, IOUtils.DEFAULT_BUFFER_SIZE, target);
	}

	default void encode(InputStream source, int bufferSize, OutputStream target) throws IOException, EncodeException {
		encode(source, bufferSize, target, getCount());
	}

	default void encode(InputStream source, int bufferSize, OutputStream target, int count)
			throws IOException, EncodeException {
		byte[] value = encode(source, bufferSize, count);
		target.write(value);
		target.flush();
	}

	byte[] encode(InputStream source, int bufferSize, int count) throws IOException, EncodeException;
}
