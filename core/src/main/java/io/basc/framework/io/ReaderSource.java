package io.basc.framework.io;

import java.io.IOException;
import java.io.Reader;

@FunctionalInterface
public interface ReaderSource {

	Reader getReader() throws IOException;

	default String getString() throws IOException {
		Reader reader = getReader();
		try {
			return IOUtils.read(reader);
		} finally {
			reader.close();
		}
	}
}
