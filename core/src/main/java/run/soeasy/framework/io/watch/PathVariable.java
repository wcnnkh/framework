package run.soeasy.framework.io.watch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@FunctionalInterface
public interface PathVariable extends FileVariable {
	Path getPath();

	@Override
	default File getFile() {
		return getPath().toFile();
	}

	@Override
	default InputStream getInputStream() throws IOException {
		Path path = getPath();
		if (Files.isDirectory(path)) {
			throw new FileNotFoundException(path + " (is a directory)");
		}

		return Files.newInputStream(path);
	}

	@Override
	default OutputStream getOutputStream() throws IOException {
		Path path = getPath();
		if (Files.isDirectory(path)) {
			throw new FileNotFoundException(path + " (is a directory)");
		}
		return Files.newOutputStream(path);
	}

	@Override
	default ReadableByteChannel readableChannel() throws IOException {
		return Files.newByteChannel(getPath(), StandardOpenOption.READ);
	}

	@Override
	default WritableByteChannel writableChannel() throws IOException {
		return Files.newByteChannel(getPath(), StandardOpenOption.WRITE);
	}
}
