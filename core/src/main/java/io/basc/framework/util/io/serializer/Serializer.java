package io.basc.framework.util.io.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.codec.Codec;
import io.basc.framework.util.codec.DecodeException;
import io.basc.framework.util.codec.EncodeException;
import io.basc.framework.util.codec.support.SerializerCodec;
import io.basc.framework.util.codec.support.ToBytesCodec;
import io.basc.framework.util.io.IOUtils;
import io.basc.framework.util.io.UnsafeByteArrayInputStream;
import io.basc.framework.util.io.UnsafeByteArrayOutputStream;
import lombok.NonNull;

/**
 * 序列化与反序列化
 * 
 * @author wcnnkh
 *
 */
public interface Serializer extends ToBytesCodec<Object>, CrossLanguageSerializer {

	void serialize(Object source, OutputStream target) throws IOException;

	@Override
	default void serialize(Object source, TypeDescriptor sourceTypeDescriptor, OutputStream target) throws IOException {
		serialize(source, target);
	}

	@Override
	default <T> T deserialize(InputStream source, TypeDescriptor targetTypeDescriptor)
			throws IOException, DecodeException {
		try {
			return deserialize(source);
		} catch (ClassNotFoundException e) {
			throw new DecodeException(e);
		}
	}

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

	default <T> T clone(@NonNull T source) {
		try {
			byte[] data = serialize(source);
			return deserialize(data);
		} catch (Exception e) {
			// 不可能存在此错误
			throw new SerializerException("This error is not possible", e);
		}
	}
}
