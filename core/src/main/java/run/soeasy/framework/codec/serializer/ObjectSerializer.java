package run.soeasy.framework.codec.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import run.soeasy.framework.codec.io.DirectSerializer;
import run.soeasy.framework.core.convert.TypeDescriptor;

public interface ObjectSerializer extends SerializationConverter, DirectSerializer<Object> {
	@Override
	default void serialize(Object source, TypeDescriptor sourceTypeDescriptor, OutputStream target) throws IOException {
		serialize(source, target);
	}

	@Override
	default Object deserialize(InputStream source, TypeDescriptor targetTypeDescriptor)
			throws IOException, SerializerException {
		try {
			return deserialize(source);
		} catch (ClassNotFoundException e) {
			// 将类未找到异常转换为序列化异常（符合接口规范）
			throw new SerializerException("Failed to deserialize: class not found", e);
		}
	}

	@Override
	default Object clone(Object source) {
		return DirectSerializer.super.clone(source);
	}
}