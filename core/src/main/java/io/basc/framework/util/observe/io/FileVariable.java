package io.basc.framework.util.observe.io;

import java.io.File;
import java.io.IOException;

import io.basc.framework.util.observe.Variable;

public interface FileVariable extends Variable {
	File getFile() throws IOException;

	@Override
	default long lastModified() throws IOException {
		File file = getFile();
		return file == null ? 0L : file.lastModified();
	}
}
