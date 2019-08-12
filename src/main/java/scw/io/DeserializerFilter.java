package scw.io;

import java.io.IOException;
import java.io.InputStream;

public interface DeserializerFilter {
	Object deserialize(Class<?> type, InputStream input, DeserializerFilterChain chain)  throws IOException;
}
