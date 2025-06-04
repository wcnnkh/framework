package run.soeasy.framework.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.codec.binary.ToBytesCodec;
import run.soeasy.framework.codec.lang.SerializerCodec;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.io.IOUtils;

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
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try {
			serialize(data, target);
			return target.toByteArray();
		} catch (IOException e) {
			throw new SerializerException(e);
		} finally {
			IOUtils.closeQuietly(target);
		}
	}

	<T> T deserialize(InputStream input, int bufferSize) throws IOException, ClassNotFoundException;

	default <T> T deserialize(InputStream input) throws IOException, ClassNotFoundException {
		return deserialize(input, IOUtils.DEFAULT_BUFFER_SIZE);
	}

	default <T> T deserialize(byte[] data) throws ClassNotFoundException, SerializerException {
		ByteArrayInputStream input = new ByteArrayInputStream(data);
		try {
			return deserialize(input);
		} catch (IOException e) {
			throw new SerializerException(e);
		} finally {
			IOUtils.closeQuietly(input);
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
