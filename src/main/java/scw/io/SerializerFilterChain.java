package scw.io;

import java.io.IOException;
import java.io.OutputStream;

public interface SerializerFilterChain {
	void doSerialize(Class<?> type, Object data, OutputStream output) throws IOException;
}
