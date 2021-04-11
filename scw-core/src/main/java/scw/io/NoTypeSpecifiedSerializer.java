package scw.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 不指定类型的序列化
 * @author shuchaowen
 *
 */
public interface NoTypeSpecifiedSerializer {
	void serialize(OutputStream out, Object data) throws IOException;

	default byte[] serialize(Object data) throws SerializerException{
		UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream();
		try {
			serialize(out, data);
			return out.toByteArray();
		} catch (IOException e) {
			// 不应该存在此错误
			throw new SerializerException(e);
		} finally {
			out.close();
		}
	}
	
	<T> T deserialize(InputStream input) throws IOException, ClassNotFoundException;

	default <T> T deserialize(byte[] data) throws ClassNotFoundException, SerializerException{
		UnsafeByteArrayInputStream input = new UnsafeByteArrayInputStream(data);
		try {
			return deserialize(input);
		} catch (IOException e) {
			// 不应该存在此错误
			throw new SerializerException(e);
		} finally {
			input.close();
		}
	}
}
