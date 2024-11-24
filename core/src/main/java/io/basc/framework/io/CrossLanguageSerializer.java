package io.basc.framework.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.basc.framework.core.convert.TypeDescriptor;

/**
 * 跨语言的序列化和反序列化
 * 
 * @author wcnnkh
 *
 */
public interface CrossLanguageSerializer {

	void serialize(Object source, TypeDescriptor sourceTypeDescriptor, OutputStream target) throws IOException;

	default byte[] serialize(Object source, TypeDescriptor sourceTypeDescriptor) {
		UnsafeByteArrayOutputStream target = new UnsafeByteArrayOutputStream();
		try {
			serialize(source, sourceTypeDescriptor, target);
			return target.toByteArray();
		} catch (IOException e) {
			throw new SerializerException(e);
		} finally {
			target.close();
		}
	}

	<T> T deserialize(InputStream source, TypeDescriptor targetTypeDescriptor) throws IOException;

	default <T> T deserialize(byte[] data, TypeDescriptor targetTypeDescriptor) {
		UnsafeByteArrayInputStream input = new UnsafeByteArrayInputStream(data);
		try {
			return deserialize(input, targetTypeDescriptor);
		} catch (IOException e) {
			throw new SerializerException(e);
		} finally {
			input.close();
		}
	}

	default Serializer toSerializer(TypeDescriptor typeDescriptor) {
		return new ObjectSerializer(this, typeDescriptor);
	}
}
