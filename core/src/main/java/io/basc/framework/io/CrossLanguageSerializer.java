package io.basc.framework.io;

import io.basc.framework.convert.TypeDescriptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 跨语言的序列化和反序列化
 * 
 * @author shuchaowen
 *
 */
public interface CrossLanguageSerializer {

	void serialize(OutputStream out, TypeDescriptor type, Object data) throws IOException;

	default byte[] serialize(TypeDescriptor type, Object data) {
		UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream();
		try {
			serialize(out, type, data);
			return out.toByteArray();
		} catch (IOException e) {
			throw new SerializerException(e);
		} finally {
			out.close();
		}
	}

	<T> T deserialize(InputStream input, TypeDescriptor type) throws IOException;

	default <T> T deserialize(byte[] data, TypeDescriptor type) {
		UnsafeByteArrayInputStream input = new UnsafeByteArrayInputStream(data);
		try {
			return deserialize(input, type);
		} catch (IOException e) {
			throw new SerializerException(e);
		} finally {
			input.close();
		}
	}
}
