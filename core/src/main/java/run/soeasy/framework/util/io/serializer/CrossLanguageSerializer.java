package run.soeasy.framework.util.io.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.util.io.IOUtils;

/**
 * 跨语言的序列化和反序列化
 * 
 * @author wcnnkh
 *
 */
public interface CrossLanguageSerializer {

	void serialize(Object source, TypeDescriptor sourceTypeDescriptor, OutputStream target) throws IOException;

	default byte[] serialize(Object source, TypeDescriptor sourceTypeDescriptor) {
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

	<T> T deserialize(InputStream source, TypeDescriptor targetTypeDescriptor) throws IOException;

	default <T> T deserialize(byte[] data, TypeDescriptor targetTypeDescriptor) {
		ByteArrayInputStream input = new ByteArrayInputStream(data);
		try {
			return deserialize(input, targetTypeDescriptor);
		} catch (IOException e) {
			throw new SerializerException(e);
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	default Serializer toSerializer(TypeDescriptor typeDescriptor) {
		return new ObjectSerializer(this, typeDescriptor);
	}

	default <T> T clone(@NonNull T source, @NonNull TypeDescriptor typeDescriptor) {
		byte[] data = serialize(source, typeDescriptor);
		return deserialize(data, typeDescriptor);
	}
}
