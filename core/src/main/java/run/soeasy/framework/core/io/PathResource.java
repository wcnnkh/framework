package run.soeasy.framework.core.io;

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

import lombok.NonNull;

public class PathResource implements Resource, PathVariable {
	private final Path path;

	/**
	 * Create a new PathResource from a Path handle.
	 * <p>
	 * Note: Unlike {@link FileSystemResource}, when building relative resources via
	 * {@link #createRelative}, the relative path will be built <i>underneath</i>
	 * the given root: e.g. Paths.get("C:/dir1/"), relative path "dir2" &rarr;
	 * "C:/dir1/dir2"!
	 * 
	 * @param path a Path handle
	 */
	public PathResource(@NonNull Path path) {
		this.path = path.normalize();
	}

	/**
	 * Create a new PathResource from a Path handle.
	 * <p>
	 * Note: Unlike {@link FileSystemResource}, when building relative resources via
	 * {@link #createRelative}, the relative path will be built <i>underneath</i>
	 * the given root: e.g. Paths.get("C:/dir1/"), relative path "dir2" &rarr;
	 * "C:/dir1/dir2"!
	 * 
	 * @param path a path
	 * @see java.nio.file.Paths#get(String, String...)
	 */
	public PathResource(@NonNull String path) {
		this.path = Paths.get(path).normalize();
	}

	/**
	 * Create a new PathResource from a Path handle.
	 * <p>
	 * Note: Unlike {@link FileSystemResource}, when building relative resources via
	 * {@link #createRelative}, the relative path will be built <i>underneath</i>
	 * the given root: e.g. Paths.get("C:/dir1/"), relative path "dir2" &rarr;
	 * "C:/dir1/dir2"!
	 * 
	 * @param uri a path URI
	 * @see java.nio.file.Paths#get(URI)
	 */
	public PathResource(@NonNull URI uri) {
		this.path = Paths.get(uri).normalize();
	}

	/**
	 * Return the file path for this resource.
	 */
	public final Path getPath() {
		return this.path;
	}

	/**
	 * This implementation returns whether the underlying file exists.
	 * 
	 * @see java.nio.file.Files#exists(Path, java.nio.file.LinkOption...)
	 */
	@Override
	public boolean exists() {
		return Files.exists(this.path);
	}

	/**
	 * This implementation checks whether the underlying file is marked as readable
	 * (and corresponds to an actual file with content, not to a directory).
	 * 
	 * @see java.nio.file.Files#isReadable(Path)
	 * @see java.nio.file.Files#isDirectory(Path, java.nio.file.LinkOption...)
	 */
	@Override
	public boolean isReadable() {
		return (Files.isReadable(this.path) && !Files.isDirectory(this.path));
	}

	/**
	 * This implementation opens a InputStream for the underlying file.
	 * 
	 * @see java.nio.file.spi.FileSystemProvider#newInputStream(Path, OpenOption...)
	 */
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

	/**
	 * This implementation checks whether the underlying file is marked as writable
	 * (and corresponds to an actual file with content, not to a directory).
	 * 
	 * @see java.nio.file.Files#isWritable(Path)
	 * @see java.nio.file.Files#isDirectory(Path, java.nio.file.LinkOption...)
	 */
	@Override
	public boolean isWritable() {
		return (Files.isWritable(this.path) && !Files.isDirectory(this.path));
	}

	/**
	 * This implementation opens a OutputStream for the underlying file.
	 * 
	 * @see java.nio.file.spi.FileSystemProvider#newOutputStream(Path,
	 *      OpenOption...)
	 */
	@Override
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
	@Override
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
	 * This implementation always indicates a file.
	 */
	@Override
	public boolean isFile() {
		return true;
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
	 * This implementation opens a Channel for the underlying file.
	 * 
	 * @see Files#newByteChannel(Path, OpenOption...)
	 */
	public ReadableByteChannel readableChannel() throws IOException {
		try {
			return Files.newByteChannel(this.path, StandardOpenOption.READ);
		} catch (NoSuchFileException ex) {
			throw new FileNotFoundException(ex.getMessage());
		}
	}

	/**
	 * This implementation opens a Channel for the underlying file.
	 * 
	 * @see Files#newByteChannel(Path, OpenOption...)
	 */
	public WritableByteChannel writableChannel() throws IOException {
		return Files.newByteChannel(this.path, StandardOpenOption.WRITE);
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
	public Resource createRelative(String relativePath) {
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

	@Override
	public String getDescription() {
		return "path [" + this.path.toAbsolutePath() + "]";
	}

	/**
	 * This implementation compares the underlying Path references.
	 */
	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof PathResource && this.path.equals(((PathResource) other).path)));
	}

	/**
	 * This implementation returns the hash code of the underlying Path reference.
	 */
	@Override
	public int hashCode() {
		return this.path.hashCode();
	}

}
