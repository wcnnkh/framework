package io.basc.framework.codec.decode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.Decoder;
import io.basc.framework.io.IOUtils;
import io.basc.framework.io.UnsafeByteArrayInputStream;
import io.basc.framework.io.UnsafeByteArrayOutputStream;

public interface BytesDecoder extends FromBytesDecoder<byte[]>, Decoder<byte[], byte[]> {

	default BytesDecoder toDecoder(BytesDecoder decoder) {
		return new NestedBytesDecoder(this, decoder);
	}

	default BytesDecoder fromDecoder(BytesDecoder decoder) {
		return new NestedBytesDecoder(decoder, this);
	}

	@Override
	default byte[] decode(byte[] source) throws DecodeException {
		UnsafeByteArrayOutputStream target = new UnsafeByteArrayOutputStream();
		try {
			decode(new UnsafeByteArrayInputStream(source), source.length, target);
		} catch (IOException e) {
			throw new DecodeException(e);
		}
		return target.toByteArray();
	}

	@Override
	default byte[] decode(InputStream source, int bufferSize) throws IOException, DecodeException {
		UnsafeByteArrayOutputStream target = new UnsafeByteArrayOutputStream();
		try {
			decode(source, bufferSize, target);
		} catch (IOException e) {
			throw new DecodeException(e);
		}
		return target.toByteArray();
	}

	default void decode(InputStream source, OutputStream target) throws DecodeException, IOException {
		decode(source, IOUtils.DEFAULT_BUFFER_SIZE, target);
	}

	void decode(InputStream source, int bufferSize, OutputStream target) throws DecodeException, IOException;
}
