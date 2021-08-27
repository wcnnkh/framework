package io.basc.framework.microsoft;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.Collection;

public interface ExcelExport extends Flushable, Closeable {
	void append(Collection<String> contents) throws IOException;

	void append(String... contents) throws IOException;
}
