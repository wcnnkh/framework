package scw.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 指定对象类型的序列化
 * @author shuchaowen
 *
 */
public interface SpecifiedTypeSerializer {
	<T> void serialize(OutputStream out, Class<T> type, T data) throws IOException;

	default <T> byte[] serialize(Class<T> type, T data){
		UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream();
		try {
			serialize(out, type, data);
			return out.toByteArray();
		} catch (IOException e) {
			// 不可能存在此错误
			throw new SerializerException(e);
		} finally {
			out.close();
		}
	}

	<T> T deserialize(Class<T> type, InputStream input) throws IOException;

	default <T> T deserialize(Class<T> type, byte[] data){
		UnsafeByteArrayInputStream input = new UnsafeByteArrayInputStream(data);
		try {
			return deserialize(type, input);
		} catch (IOException e) {
			// 不可能存在此错误
			throw new SerializerException(e);
		} finally {
			input.close();
		}
	}
}
