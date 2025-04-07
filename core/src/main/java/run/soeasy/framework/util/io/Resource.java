package run.soeasy.framework.util.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.util.StringUtils;
import run.soeasy.framework.util.alias.Named;
import run.soeasy.framework.util.collection.Elements;
import run.soeasy.framework.util.collection.Listable;
import run.soeasy.framework.util.math.LongValue;
import run.soeasy.framework.util.math.NumberAdder;
import run.soeasy.framework.util.math.NumberValue;

/**
 * 一个资源的定义
 * 
 * @author shuchaowen
 *
 */
public interface Resource extends InputSource<InputStream, Reader>, OutputSource<OutputStream, Writer>, FileVariable,
		Named, Listable<Resource> {

	@Data
	public static class RenamedResource<W extends Resource> implements ResourceWrapper<W> {
		@NonNull
		private final String name;
		@NonNull
		private final W source;
	}

	@FunctionalInterface
	public static interface ResourceWrapper<W extends Resource>
			extends Resource, InputSourceWrapper<InputStream, Reader, W>, OutputSourceWrapper<OutputStream, Writer, W>,
			NamedWrapper<W>, ListableWrapper<Resource, W> {
		@Override
		default String getName() {
			return getSource().getName();
		}

		@Override
		default Resource createRelative(String relativePath) throws IOException {
			return getSource().createRelative(relativePath);
		}

		@Override
		default boolean exists() {
			return getSource().exists();
		}

		@Override
		default String getDescription() {
			return getSource().getDescription();
		}

		@Override
		default File getFile() throws IOException, FileNotFoundException {
			return getSource().getFile();
		}

		@Override
		default URI getURI() throws IOException {
			return getSource().getURI();
		}

		@Override
		default URL getURL() throws IOException {
			return getSource().getURL();
		}

		@Override
		default boolean isFile() {
			return getSource().isFile();
		}

		@Override
		default long lastModified() throws IOException {
			return getSource().lastModified();
		}

		@Override
		default Resource rename(String name) {
			return getSource().rename(name);
		}

		@Override
		default NumberValue contentLength() throws IOException {
			return getSource().contentLength();
		}

		@Override
		default boolean hasElements() {
			return getSource().hasElements();
		}

		@Override
		default Elements<Resource> getElements() {
			return getSource().getElements();
		}

		@Override
		default boolean isOpen() {
			return getSource().isOpen();
		}

		@Override
		default boolean isReadable() {
			return getSource().isReadable();
		}

		@Override
		default InputStream getInputStream() throws IOException {
			return getSource().getInputStream();
		}

		@Override
		default boolean isDecoded() {
			return getSource().isDecoded();
		}

		@Override
		default Reader getReader() throws IOException {
			return getSource().getReader();
		}

		@Override
		default boolean isWritable() {
			return getSource().isWritable();
		}

		@Override
		default OutputStream getOutputStream() throws IOException {
			return getSource().getOutputStream();
		}

		@Override
		default boolean isEncoded() {
			return getSource().isEncoded();
		}

		@Override
		default Writer getWriter() throws IOException {
			return getSource().getWriter();
		}
	}

	public static final String FOLDER_SEPARATOR = "/";
	public static final String URL_PROTOCOL_FILE = "file";

	public static String getName(String path) {
		if (path == null) {
			return null;
		}
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
	}

	/**
	 * Create a URI instance for the given URL, replacing spaces with "%20" URI
	 * encoding first.
	 * 
	 * @param url the URL to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException if the URL wasn't a valid URI
	 * @see java.net.URL#toURI()
	 */
	public static URI toURI(URL url) throws URISyntaxException {
		return toURI(url.toString());
	}

	/**
	 * Create a URI instance for the given location String, replacing spaces with
	 * "%20" URI encoding first.
	 * 
	 * @param location the location String to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException if the location wasn't a valid URI
	 */
	public static URI toURI(String location) throws URISyntaxException {
		return new URI(StringUtils.replace(location, " ", "%20"));
	}

	@Override
	default String getName() {
		if (isFile()) {
			try {
				return getFile().getName();
			} catch (IOException e) {
			}
		}

		URI uri;
		try {
			uri = getURI();
		} catch (IOException e) {
			return null;
		}

		if (uri == null) {
			return null;
		}
		return getName(uri.getPath());
	}

	/**
	 * Indicate whether this resource represents a handle with an open stream. If
	 * {@code true}, the cannot be read/write multiple times, and must be
	 * read/writer and closed to avoid resource leaks.
	 **/
	default boolean isOpen() {
		return false;
	}

	default NumberValue contentLength() throws IOException {
		if (isFile()) {
			return new LongValue(getFile().length());
		}

		InputStream is = getInputStream();
		try {
			NumberAdder size = new NumberAdder();
			byte[] buf = new byte[256];
			int read;
			while ((read = is.read(buf)) != -1) {
				size.increment(read);
			}
			return size;
		} finally {
			is.close();
		}
	}

	/**
	 * 创建关联资源
	 * 
	 * @param relativePath
	 * @return
	 * @throws IOException
	 */
	default Resource createRelative(String relativePath) throws IOException {
		throw new FileNotFoundException("Cannot create a relative resource for " + getDescription());
	}

	default String getDescription() {
		try {
			return "URL [" + getURL() + "]";
		} catch (IOException e) {
		}

		try {
			return "URI [" + getURI() + "]";
		} catch (IOException e) {
		}
		return getClass().getName();
	}

	default boolean isFile() {
		try {
			URI uri = getURI();
			return URL_PROTOCOL_FILE.equals(uri.getScheme());
		} catch (IOException ex) {
		}

		try {
			URL url = getURL();
			return URL_PROTOCOL_FILE.equals(url.getProtocol());
		} catch (IOException ex) {
		}
		return false;
	}

	default File getFile() throws IOException, FileNotFoundException {
		try {
			URI uri = getURI();
			if (!URL_PROTOCOL_FILE.equals(uri.getScheme())) {
				throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path "
						+ "because it does not reside in the file system: " + uri);
			}
			return new File(uri.getSchemeSpecificPart());
		} catch (IOException e) {
		}

		try {
			URL url = getURL();
			if (!URL_PROTOCOL_FILE.equals(url.getProtocol())) {
				throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path "
						+ "because it does not reside in the file system: " + url);
			}
			try {
				return new File(toURI(url).getSchemeSpecificPart());
			} catch (URISyntaxException ex) {
				// Fallback for URLs that are not valid URIs (should hardly ever
				// happen).
				return new File(url.getFile());
			}
		} catch (IOException e) {
		}
		throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path");
	}

	/**
	 * Return a URI handle for this resource.
	 * 
	 * @throws IOException if the resource cannot be resolved as URI, i.e. if the
	 *                     resource is not available as descriptor
	 */
	default URI getURI() throws IOException {
		URL url;
		try {
			url = getURL();
		} catch (IOException e) {
			throw new IOException("Cannot be resolved as URI");
		}

		try {
			return toURI(url);
		} catch (URISyntaxException ex) {
			throw new IOException("Invalid URI [" + url + "]", ex);
		}
	}

	/**
	 * Return a URL handle for this resource.
	 * 
	 * @throws IOException if the resource cannot be resolved as URL, i.e. if the
	 *                     resource is not available as descriptor
	 */
	default URL getURL() throws IOException {
		throw new IOException("Cannot be resolved as URL");
	}

	@Override
	default Resource rename(String name) {
		return new RenamedResource<>(name, this);
	}

	@Override
	default boolean hasElements() {
		return false;
	}

	@Override
	default Elements<Resource> getElements() {
		return Elements.empty();
	}

	boolean exists();

	@Override
	default long lastModified() throws IOException {
		return isFile() ? getFile().lastModified() : 0;
	}

	@Override
	default boolean isReadable() {
		return false;
	}

	@Override
	default InputStream getInputStream() throws IOException {
		throw new IOException("Cannot readable resources");
	}

	@Override
	default boolean isDecoded() {
		return false;
	}

	@Override
	default Reader getReader() throws IOException {
		throw new IOException("Cannot decode resources");
	}

	@Override
	default boolean isWritable() {
		return false;
	}

	@Override
	default OutputStream getOutputStream() throws IOException {
		throw new IOException("Cannot writable resources");
	}

	@Override
	default boolean isEncoded() {
		return false;
	}

	@Override
	default Writer getWriter() throws IOException {
		throw new IOException("Cannot encode resources");
	}

}
