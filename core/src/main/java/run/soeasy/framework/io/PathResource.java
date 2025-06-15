package run.soeasy.framework.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.io.watch.PathVariable;

@Data
class PathResource implements Resource, PathVariable {
	@NonNull
	private final Path path;

	@Override
	public boolean exists() {
		return Files.exists(this.path);
	}

	@Override
	public boolean isReadable() {
		return (Files.isReadable(this.path) && !Files.isDirectory(this.path));
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (!exists()) {
			throw new FileNotFoundException(getPath() + " (no such file or directory)");
		}
		if (Files.isDirectory(this.path)) {
			throw new FileNotFoundException(getPath() + " (is a directory)");
		}

		return Files.newInputStream(this.path);
	}

	@Override
	public boolean isWritable() {
		return (Files.isWritable(this.path) && !Files.isDirectory(this.path));
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		if (Files.isDirectory(this.path)) {
			throw new FileNotFoundException(getPath() + " (is a directory)");
		}
		return Files.newOutputStream(this.path);
	}

	public ReadableByteChannel readableChannel() throws IOException {
		try {
			return Files.newByteChannel(this.path, StandardOpenOption.READ);
		} catch (NoSuchFileException ex) {
			throw new FileNotFoundException(ex.getMessage());
		}
	}

	public WritableByteChannel writableChannel() throws IOException {
		return Files.newByteChannel(this.path, StandardOpenOption.WRITE);
	}

	@Override
	public long contentLength() throws IOException {
		return Files.size(this.path);
	}

	@Override
	public long lastModified() throws IOException {
		return Files.getLastModifiedTime(this.path).toMillis();
	}

	@Override
	public String getName() {
		return this.path.getFileName().toString();
	}

	@Override
	public String getDescription() {
		return "path [" + this.path.toAbsolutePath() + "]";
	}
}
