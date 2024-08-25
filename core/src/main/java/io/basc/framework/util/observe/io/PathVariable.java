package io.basc.framework.util.observe.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface PathVariable extends FileVariable {
	Path getPath();

	@Override
	default File getFile() throws IOException {
		return getPath().toFile();
	}
}
