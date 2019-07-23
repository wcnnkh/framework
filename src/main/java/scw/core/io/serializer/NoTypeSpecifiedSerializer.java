package scw.core.io.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface NoTypeSpecifiedSerializer {
	void serialize(OutputStream out, Object data) throws IOException;

	byte[] serialize(Object data);
	
	<T> T deserialize(InputStream input) throws IOException;

	<T> T deserialize(byte[] data);
}
