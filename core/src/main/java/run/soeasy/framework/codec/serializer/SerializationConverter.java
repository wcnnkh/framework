package run.soeasy.framework.codec.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.NonNull;
import run.soeasy.framework.codec.io.Serializer;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.io.IOUtils;

public interface SerializationConverter extends ReversibleConverter<Object, byte[]> {

	default Object clone(@NonNull Object source) {
		TypeDescriptor typeDescriptor = TypeDescriptor.forObject(source);
		byte[] data = serialize(source, typeDescriptor);
		return deserialize(data, typeDescriptor);
	}

	default Object deserialize(@NonNull byte[] data, @NonNull TypeDescriptor targetTypeDescriptor)
			throws SerializerException {
		ByteArrayInputStream input = new ByteArrayInputStream(data);
		try {
			return deserialize(input, targetTypeDescriptor);
		} catch (IOException e) {
			throw new SerializerException(e);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	Object deserialize(@NonNull InputStream source, @NonNull TypeDescriptor targetTypeDescriptor) throws IOException;

	@Override
	default Object from(byte[] source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		return deserialize(source, targetTypeDescriptor);
	}

	default byte[] serialize(Object source, @NonNull TypeDescriptor sourceTypeDescriptor) throws SerializerException {
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		try {
			serialize(source, sourceTypeDescriptor, target);
			return target.toByteArray();
		} catch (IOException e) {
			throw new SerializerException(e);
		} finally {
			IOUtils.closeQuietly(target);
		}
	}

	void serialize(Object source, @NonNull TypeDescriptor sourceTypeDescriptor, @NonNull OutputStream target)
			throws IOException;

	@Override
	default byte[] to(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		return serialize(source, sourceTypeDescriptor);
	}

	default <T> Serializer<T> typed(@NonNull TypeDescriptor typeDescriptor) {
		return new TypedSerializer<>(this, typeDescriptor);
	}

	default <T> Serializer<T> typed(@NonNull Class<T> type) {
		return typed(TypeDescriptor.valueOf(type));
	}
}