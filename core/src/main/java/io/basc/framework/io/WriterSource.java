package io.basc.framework.io;

import java.io.IOException;
import java.io.Writer;

@FunctionalInterface
public interface WriterSource {
	Writer getWriter() throws IOException;
}
