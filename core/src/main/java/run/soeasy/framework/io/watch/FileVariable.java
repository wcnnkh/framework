package run.soeasy.framework.io.watch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import run.soeasy.framework.io.FileUtils;
import run.soeasy.framework.io.Resource;

@FunctionalInterface
public interface FileVariable extends Resource {
	File getFile();

	@Override
	default long lastModified() throws IOException {
		File file = getFile();
		return file == null ? 0L : file.lastModified();
	}

	@Override
	default InputStream getInputStream() throws IOException {
		return FileUtils.openInputStream(getFile());
	}

	@Override
	default OutputStream getOutputStream() throws IOException {
		return FileUtils.openOutputStream(getFile());
	}

	@Override
	default boolean exists() {
		return getFile().exists();
	}

	@Override
	default boolean isReadable() {
		File file = getFile();
		return file.canWrite() && !file.isDirectory();
	}

	@Override
	default boolean isWritable() {
		File file = getFile();
		return file.canWrite() && !file.isDirectory();
	}

	@Override
	default long contentLength() throws IOException {
		File file = getFile();
		long length = file.length();
		if (length == 0L && !file.exists()) {
			throw new FileNotFoundException(
					getDescription() + " cannot be resolved in the file system for checking its content length");
		}
		return length;
	}

	@Override
	default String getName() {
		return getFile().getName();
	}

	@Override
	default String getDescription() {
		return "file [" + getFile().getAbsolutePath() + "]";
	}
}
