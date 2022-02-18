package io.basc.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.codec.Codec;
import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.codec.support.SerializerCodec;
import io.basc.framework.codec.support.ToBytesCodec;

/**
 * 序列化与反序列化
 * 
 * @author wcnnkh
 *
 */
public interface Serializer extends ToBytesCodec<Object> {

	void serialize(Object source, OutputStream target) throws IOException;

	@Override
	default void encode(Object source, OutputStream target) throws IOException, EncodeException {
		serialize(source, target);
	}

	default byte[] serialize(Object data) throws SerializerException {
		UnsafeByteArrayOutputStream target = new UnsafeByteArrayOutputStream();
		try {
			serialize(data, target);
			return target.toByteArray();
		} catch (IOException e) {
			throw new SerializerException(e);
		} finally {
			target.close();
		}
	}

	<T> T deserialize(InputStream input, int bufferSize) throws IOException, ClassNotFoundException;

	default <T> T deserialize(InputStream input) throws IOException, ClassNotFoundException {
		return deserialize(input, IOUtils.DEFAULT_BUFFER_SIZE);
	}

	default <T> T deserialize(byte[] data) throws ClassNotFoundException, SerializerException {
		UnsafeByteArrayInputStream input = new UnsafeByteArrayInputStream(data);
		try {
			return deserialize(input);
		} catch (IOException e) {
			throw new SerializerException(e);
		} finally {
			input.close();
		}
	}

	@Override
	default byte[] encode(Object source) throws EncodeException {
		return serialize(source);
	}

	@Override
	default Object decode(InputStream source, int bufferSize) throws IOException, DecodeException {
		try {
			return deserialize(source, bufferSize);
		} catch (ClassNotFoundException e) {
			throw new DecodeException(e);
		}
	}

	default <D> Codec<D, byte[]> toCodec() {
		return new SerializerCodec<D>(this);
	}
}
