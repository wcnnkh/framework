package scw.io;

import java.io.IOException;
import java.io.InputStream;

public interface DeserializerFilterChain {
	Object doDeserialize(Class<?> type, InputStream input) throws IOException;
}
