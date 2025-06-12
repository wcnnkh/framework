package run.soeasy.framework.io.file;

import java.io.File;
import java.io.IOException;

import run.soeasy.framework.io.watch.Variable;

public interface FileVariable extends Variable {
	File getFile() throws IOException;

	@Override
	default long lastModified() throws IOException {
		File file = getFile();
		return file == null ? 0L : file.lastModified();
	}
}
