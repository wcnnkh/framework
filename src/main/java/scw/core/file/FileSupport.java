package scw.core.file;

import java.io.File;
import java.io.IOException;

public interface FileSupport {
	void copyFile(File srcFile, File destFile, boolean preserveFileDate)
			throws IOException;
}
