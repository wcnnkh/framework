package io.basc.framework.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import io.basc.framework.util.Assert;

/**
 * java7的Path实现
 * 
 * @author shuchaowen
 *
 */
public class PathResource extends AbstractResource implements WritableResource {

	private final Path path;

	public PathResource(Path path) {
		Assert.notNull(path, "Path must not be null");
		this.path = path.normalize();
	}

	public PathResource(String path) {
		Assert.notNull(path, "Path must not be null");
		this.path = Paths.get(path).normalize();
	}

	public PathResource(URI uri) {
		Assert.notNull(uri, "URI must not be null");
		this.path = Paths.get(uri).normalize();
	}

	public final String getPath() {
		return this.path.toString();
	}

	@Override
	public boolean exists() {
		return Files.exists(this.path);
	}

	@Override
	public boolean isReadable() {
		return (Files.isReadable(this.path) && !Files.isDirectory(this.path));
	}

	public InputStream getInputStream() throws IOException {
		if (!exists()) {
			throw new FileNotFoundException(getPath() + " (no such file or directory)");
		}
		if (Files.isDirectory(this.path)) {
			throw new FileNotFoundException(getPath() + " (is a directory)");
		}
		return Files.newInputStream(this.path);
	}

	/**
	 * This implementation checks whether the underlying file is marked as writable
	 * (and corresponds to an actual file with content, not to a directory).
	 * 
	 * @see java.nio.file.Files#isWritable(Path)
	 * @see java.nio.file.Files#isDirectory(Path, java.nio.file.LinkOption...)
	 */
	public boolean isWritable() {
		return (Files.isWritable(this.path) && !Files.isDirectory(this.path));
	}

	@Override
	public boolean isFile() {
		return true;
	}

	/**
	 * This implementation opens a OutputStream for the underlying file.
	 * 
	 * @see java.nio.file.spi.FileSystemProvider#newOutputStream(Path,
	 *      OpenOption...)
	 */
	public OutputStream getOutputStream() throws IOException {
		if (Files.isDirectory(this.path)) {
			throw new FileNotFoundException(getPath() + " (is a directory)");
		}
		return Files.newOutputStream(this.path);
	}

	/**
	 * This implementation returns a URL for the underlying file.
	 * 
	 * @see java.nio.file.Path#toUri()
	 * @see java.net.URI#toURL()
	 */
	public URL getURL() throws IOException {
		return this.path.toUri().toURL();
	}

	/**
	 * This implementation returns a URI for the underlying file.
	 * 
	 * @see java.nio.file.Path#toUri()
	 */
	@Override
	public URI getURI() throws IOException {
		return this.path.toUri();
	}

	/**
	 * This implementation returns the underlying File reference.
	 */
	@Override
	public File getFile() throws IOException {
		try {
			return this.path.toFile();
		} catch (UnsupportedOperationException ex) {
			// Only paths on the default file system can be converted to a File:
			// Do exception translation for cases where conversion is not possible.
			throw new FileNotFoundException(this.path + " cannot be resolved to absolute file path");
		}
	}

	/**
	 * This implementation returns the underlying file's length.
	 */
	@Override
	public long contentLength() throws IOException {
		return Files.size(this.path);
	}

	/**
	 * This implementation returns the underlying File's timestamp.
	 * 
	 * @see java.nio.file.Files#getLastModifiedTime(Path,
	 *      java.nio.file.LinkOption...)
	 */
	@Override
	public long lastModified() throws IOException {
		// We can not use the superclass method since it uses conversion to a File and
		// only a Path on the default file system can be converted to a File...
		return Files.getLastModifiedTime(this.path).toMillis();
	}

	/**
	 * This implementation creates a PathResource, applying the given path relative
	 * to the path of the underlying file of this resource descriptor.
	 * 
	 * @see java.nio.file.Path#resolve(String)
	 */
	@Override
	public Resource createRelative(String relativePath) throws IOException {
		return new PathResource(this.path.resolve(relativePath));
	}

	/**
	 * This implementation returns the name of the file.
	 * 
	 * @see java.nio.file.Path#getFileName()
	 */
	@Override
	public String getName() {
		return this.path.getFileName().toString();
	}

	public String getDescription() {
		return "path [" + this.path.toAbsolutePath() + "]";
	}

	/**
	 * This implementation compares the underlying Path references.
	 */
	@Override
	public boolean equals(Object obj) {
		return (this == obj || (obj instanceof PathResource && this.path.equals(((PathResource) obj).path)));
	}

	/**
	 * This implementation returns the hash code of the underlying Path reference.
	 */
	@Override
	public int hashCode() {
		return this.path.hashCode();
	}

	@Override
	public WritableByteChannel writableChannel() throws IOException {
		return Files.newByteChannel(this.path, StandardOpenOption.WRITE);
	}

	@Override
	public ReadableByteChannel readableChannel() throws IOException {
		try {
			return Files.newByteChannel(this.path, StandardOpenOption.READ);
		} catch (NoSuchFileException ex) {
			throw new FileNotFoundException(ex.getMessage());
		}
	}
}
