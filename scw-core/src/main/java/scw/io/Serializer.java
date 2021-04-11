package scw.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 序列化
 * 
 * @author shuchaowen
 *
 */
public interface Serializer extends NoTypeSpecifiedSerializer,
		SpecifiedTypeSerializer {
	@Override
	default <T> void serialize(OutputStream out, Class<T> type, T data)
			throws IOException {
		serialize(out, data);
	}

	@Override
	default <T> T deserialize(Class<T> type, InputStream input)
			throws IOException, SerializerException {
		try {
			return deserialize(input);
		} catch (ClassNotFoundException e) {
			throw new SerializerException(type.toString(), e);
		}
	}
}
