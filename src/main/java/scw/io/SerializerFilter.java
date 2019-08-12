package scw.io;

import java.io.IOException;
import java.io.OutputStream;

public interface SerializerFilter {
	void serialize(Class<?> type, Object data, OutputStream output, SerializerFilterChain chain) throws IOException;
}
