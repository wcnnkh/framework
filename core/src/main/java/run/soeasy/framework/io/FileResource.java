package run.soeasy.framework.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.io.watch.FileVariable;

@Data
class FileResource implements Resource, FileVariable {
	@NonNull
	private final File file;

	@Override
	public boolean exists() {
		return this.file.exists();
	}

	@Override
	public boolean isReadable() {
		return this.file.canRead() && !this.file.isDirectory();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}

	@Override
	public boolean isWritable() {
		return this.file.canWrite() && !this.file.isDirectory();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return new FileOutputStream(file);
	}

	@Override
	public long contentLength() throws IOException {
		long length = this.file.length();
		if (length == 0L && !this.file.exists()) {
			throw new FileNotFoundException(
					getDescription() + " cannot be resolved in the file system for checking its content length");
		}
		return length;
	}

	@Override
	public long lastModified() throws IOException {
		return file.lastModified();
	}

	@Override
	public String getName() {
		return this.file.getName();
	}

	@Override
	public String getDescription() {
		return "file [" + this.file.getAbsolutePath() + "]";
	}
}
