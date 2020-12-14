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

	byte[] serialize(Object data);
	
	<T> T deserialize(InputStream input) throws IOException, ClassNotFoundException;

	<T> T deserialize(byte[] data) throws ClassNotFoundException;
}
