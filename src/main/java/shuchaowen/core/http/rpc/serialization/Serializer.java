package shuchaowen.core.http.rpc.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Serializer {
	void encode(OutputStream out, Object data) throws IOException;
	
	<T> T decode(InputStream in, Class<T> type) throws IOException;
}
