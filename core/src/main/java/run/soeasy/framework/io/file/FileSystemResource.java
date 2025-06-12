package run.soeasy.framework.io.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.io.AbstractResource;
import run.soeasy.framework.io.Resource;

public class FileSystemResource extends AbstractResource {

	private final String path;

	private final File file;

	private final Path filePath;

	/**
	 * Create a new {@code FileSystemResource} from a file path.
	 * <p>
	 * Note: When building relative resources via {@link #createRelative}, it makes
	 * a difference whether the specified resource base path here ends with a slash
	 * or not. In the case of "C:/dir1/", relative paths will be built underneath
	 * that root: e.g. relative path "dir2" &rarr; "C:/dir1/dir2". In the case of
	 * "C:/dir1", relative paths will apply at the same directory level: relative
	 * path "dir2" &rarr; "C:/dir2".
	 * 
	 * @param path a file path
	 * @see #FileSystemResource(Path)
	 */
	public FileSystemResource(@NonNull String path) {
		this.path = StringUtils.cleanPath(path);
		this.file = new File(path);
		this.filePath = this.file.toPath();
	}

	/**
	 * Create a new {@code FileSystemResource} from a {@link File} handle.
	 * <p>
	 * Note: When building relative resources via {@link #createRelative}, the
	 * relative path will apply <i>at the same directory level</i>: e.g. new
	 * File("C:/dir1"), relative path "dir2" &rarr; "C:/dir2"! If you prefer to have
	 * relative paths built underneath the given root directory, use the
	 * {@link #FileSystemResource(String) constructor with a file path} to append a
	 * trailing slash to the root path: "C:/dir1/", which indicates this directory
	 * as root for all relative paths.
	 * 
	 * @param file a File handle
	 * @see #FileSystemResource(Path)
	 * @see #getFile()
	 */
	public FileSystemResource(@NonNull File file) {
		this.path = StringUtils.cleanPath(file.getPath());
		this.file = file;
		this.filePath = file.toPath();
	}

	/**
	 * Create a new {@code FileSystemResource} from a {@link Path} handle,
	 * performing all file system interactions via NIO.2 instead of {@link File}.
	 * <p>
	 * In contrast to {@link PathResource}, this variant strictly follows the
	 * general {@link FileSystemResource} conventions, in particular in terms of
	 * path cleaning and {@link #createRelative(String)} handling.
	 * <p>
	 * Note: When building relative resources via {@link #createRelative}, the
	 * relative path will apply <i>at the same directory level</i>: e.g.
	 * Paths.get("C:/dir1"), relative path "dir2" &rarr; "C:/dir2"! If you prefer to
	 * have relative paths built underneath the given root directory, use the
	 * {@link #FileSystemResource(String) constructor with a file path} to append a
	 * trailing slash to the root path: "C:/dir1/", which indicates this directory
	 * as root for all relative paths. Alternatively, consider using
	 * {@link PathResource#PathResource(Path)} for {@code java.nio.path.Path}
	 * resolution in {@code createRelative}, always nesting relative paths.
	 * 
	 * @param filePath a Path handle to a file
	 * @see #FileSystemResource(File)
	 */
	public FileSystemResource(@NonNull Path filePath) {
		this.path = StringUtils.cleanPath(filePath.toString());
		this.file = null;
		this.filePath = filePath;
	}

	/**
	 * Create a new {@code FileSystemResource} from a {@link FileSystem} handle,
	 * locating the specified path.
	 * <p>
	 * This is an alternative to {@link #FileSystemResource(String)}, performing all
	 * file system interactions via NIO.2 instead of {@link File}.
	 * 
	 * @param fileSystem the FileSystem to locate the path within
	 * @param path       a file path
	 * @see #FileSystemResource(File)
	 */
	public FileSystemResource(@NonNull FileSystem fileSystem, @NonNull String path) {
		this.path = StringUtils.cleanPath(path);
		this.file = null;
		this.filePath = fileSystem.getPath(this.path).normalize();
	}

	/**
	 * Return the file path for this resource.
	 */
	public final String getPath() {
		return this.path;
	}

	/**
	 * This implementation returns whether the underlying file exists.
	 * 
	 * @see java.io.File#exists()
	 */
	@Override
	public boolean exists() {
		return (this.file != null ? this.file.exists() : Files.exists(this.filePath));
	}

	/**
	 * This implementation checks whether the underlying file is marked as readable
	 * (and corresponds to an actual file with content, not to a directory).
	 * 
	 * @see java.io.File#canRead()
	 * @see java.io.File#isDirectory()
	 */
	@Override
	public boolean isReadable() {
		return (this.file != null ? this.file.canRead() && !this.file.isDirectory()
				: Files.isReadable(this.filePath) && !Files.isDirectory(this.filePath));
	}

	/**
	 * This implementation opens a NIO file stream for the underlying file.
	 * 
	 * @see java.io.FileInputStream
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		try {
			return Files.newInputStream(this.filePath);
		} catch (NoSuchFileException ex) {
			throw new FileNotFoundException(ex.getMessage());
		}
	}

	public boolean delete() throws IOException {
		return Files.deleteIfExists(this.filePath);
	}

	/**
	 * This implementation checks whether the underlying file is marked as writable
	 * (and corresponds to an actual file with content, not to a directory).
	 * 
	 * @see java.io.File#canWrite()
	 * @see java.io.File#isDirectory()
	 */
	@Override
	public boolean isWritable() {
		return (this.file != null ? this.file.canWrite() && !this.file.isDirectory()
				: Files.isWritable(this.filePath) && !Files.isDirectory(this.filePath));
	}

	/**
	 * This implementation opens a FileOutputStream for the underlying file.
	 * 
	 * @see java.io.FileOutputStream
	 */
	@Override
	public OutputStream getOutputStream() throws IOException {
		return Files.newOutputStream(this.filePath);
	}

	/**
	 * This implementation returns a URL for the underlying file.
	 * 
	 * @see java.io.File#toURI()
	 */
	@Override
	public URL getURL() throws IOException {
		return (this.file != null ? this.file.toURI().toURL() : this.filePath.toUri().toURL());
	}

	/**
	 * This implementation returns a URI for the underlying file.
	 * 
	 * @see java.io.File#toURI()
	 */
	@Override
	public URI getURI() throws IOException {
		return (this.file != null ? this.file.toURI() : this.filePath.toUri());
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
	public File getFile() {
		return (this.file != null ? this.file : this.filePath.toFile());
	}

	/**
	 * This implementation opens a FileChannel for the underlying file.
	 * 
	 * @see java.nio.channels.FileChannel
	 */
	public ReadableByteChannel readableChannel() throws IOException {
		try {
			return FileChannel.open(this.filePath, StandardOpenOption.READ);
		} catch (NoSuchFileException ex) {
			throw new FileNotFoundException(ex.getMessage());
		}
	}

	/**
	 * This implementation opens a FileChannel for the underlying file.
	 * 
	 * @see java.nio.channels.FileChannel
	 */
	public WritableByteChannel writableChannel() throws IOException {
		return FileChannel.open(this.filePath, StandardOpenOption.WRITE);
	}

	/**
	 * This implementation returns the underlying File/Path length.
	 */
	@Override
	public long contentLength() throws IOException {
		if (this.file != null) {
			long length = this.file.length();
			if (length == 0L && !this.file.exists()) {
				throw new FileNotFoundException(
						getDescription() + " cannot be resolved in the file system for checking its content length");
			}
			return length;
		} else {
			try {
				return Files.size(this.filePath);
			} catch (NoSuchFileException ex) {
				throw new FileNotFoundException(ex.getMessage());
			}
		}
	}

	/**
	 * This implementation returns the underlying File/Path last-modified time.
	 */
	@Override
	public long lastModified() throws IOException {
		if (this.file != null) {
			return file.lastModified();
		} else {
			try {
				return Files.getLastModifiedTime(this.filePath).toMillis();
			} catch (NoSuchFileException ex) {
				throw new FileNotFoundException(ex.getMessage());
			}
		}
	}

	/**
	 * This implementation creates a FileSystemResource, applying the given path
	 * relative to the path of the underlying file of this resource descriptor.
	 * 
	 * @see run.soeasy.framework.core.StringUtils#applyRelativePath(String, String)
	 */
	@Override
	public Resource createRelative(String relativePath) {
		String pathToUse = StringUtils.applyRelativePath(this.path, relativePath);
		return (this.file != null ? new FileSystemResource(pathToUse)
				: new FileSystemResource(this.filePath.getFileSystem(), pathToUse));
	}

	/**
	 * This implementation returns the name of the file.
	 * 
	 * @see java.io.File#getName()
	 */
	@Override
	public String getName() {
		return (this.file != null ? this.file.getName() : this.filePath.getFileName().toString());
	}

	/**
	 * This implementation returns a description that includes the absolute path of
	 * the file.
	 * 
	 * @see java.io.File#getAbsolutePath()
	 */
	@Override
	public String getDescription() {
		return "file [" + (this.file != null ? this.file.getAbsolutePath() : this.filePath.toAbsolutePath()) + "]";
	}

	/**
	 * This implementation compares the underlying File references.
	 */
	@Override
	public boolean equals(Object other) {
		return (this == other
				|| (other instanceof FileSystemResource && this.path.equals(((FileSystemResource) other).path)));
	}

	/**
	 * This implementation returns the hash code of the underlying File reference.
	 */
	@Override
	public int hashCode() {
		return this.path.hashCode();
	}

}
