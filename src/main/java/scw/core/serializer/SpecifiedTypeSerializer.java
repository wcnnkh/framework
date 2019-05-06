package scw.core.serializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface SpecifiedTypeSerializer {
	void serialize(OutputStream out, Object data) throws IOException;

	byte[] serialize(Object data);
	
	<T> T deserialize(Class<T> type, InputStream input) throws IOException;

	<T> T deserialize(Class<T> type, byte[] data);
}
