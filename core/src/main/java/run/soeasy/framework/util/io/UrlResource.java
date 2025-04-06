package run.soeasy.framework.util.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import lombok.NonNull;
import run.soeasy.framework.util.Assert;
import run.soeasy.framework.util.StringUtils;

public class UrlResource extends AbstractResource {
	private class InternalInputStream extends InputStream {
		private URLConnection urlConnection;
		private InputStream inputStream;

		public InputStream getInputStream() throws IOException {
			if (urlConnection == null) {
				urlConnection = connect(true);
			}

			if (inputStream == null) {
				inputStream = urlConnection.getInputStream();
			}
			return inputStream;
		}

		@Override
		public int read() throws IOException {
			return getInputStream().read();
		}

		@Override
		public int read(byte[] b) throws IOException {
			return getInputStream().read(b);
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			return getInputStream().read(b, off, len);
		}

		@Override
		public void close() throws IOException {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} finally {
				if (urlConnection != null) {
					if (urlConnection instanceof HttpURLConnection) {
						((HttpURLConnection) urlConnection).disconnect();
					}
				}
			}
		}
	}

	/**
	 * Original URI, if available; used for URI and File access.
	 */
	private final URI uri;

	/**
	 * Original URL, used for actual access.
	 */
	private final URL url;

	/**
	 * Cleaned URL (with normalized path), used for comparisons.
	 */
	private volatile URL cleanedUrl;

	/**
	 * Create a new {@code UrlResource} based on the given URI object.
	 * 
	 * @param uri a URI
	 * @throws MalformedURLException if the given URL path is not valid
	 */
	public UrlResource(URI uri) throws MalformedURLException {
		Assert.notNull(uri, "URI must not be null");
		this.uri = uri;
		this.url = uri.toURL();
	}

	/**
	 * Create a new {@code UrlResource} based on the given URL object.
	 * 
	 * @param url a URL
	 */
	public UrlResource(URL url) {
		Assert.notNull(url, "URL must not be null");
		this.uri = null;
		this.url = url;
	}

	/**
	 * Create a new {@code UrlResource} based on a URL path.
	 * <p>
	 * Note: The given path needs to be pre-encoded if necessary.
	 * 
	 * @param path a URL path
	 * @throws MalformedURLException if the given URL path is not valid
	 * @see java.net.URL#URL(String)
	 */
	public UrlResource(String path) throws MalformedURLException {
		Assert.notNull(path, "Path must not be null");
		this.uri = null;
		this.url = new URL(path);
		this.cleanedUrl = getCleanedUrl(this.url, path);
	}

	/**
	 * Create a new {@code UrlResource} based on a URI specification.
	 * <p>
	 * The given parts will automatically get encoded if necessary.
	 * 
	 * @param protocol the URL protocol to use (e.g. "jar" or "file" - without
	 *                 colon); also known as "scheme"
	 * @param location the location (e.g. the file path within that protocol); also
	 *                 known as "scheme-specific part"
	 * @throws MalformedURLException if the given URL specification is not valid
	 * @see java.net.URI#URI(String, String, String)
	 */
	public UrlResource(String protocol, String location) throws MalformedURLException {
		this(protocol, location, null);
	}

	/**
	 * Create a new {@code UrlResource} based on a URI specification.
	 * <p>
	 * The given parts will automatically get encoded if necessary.
	 * 
	 * @param protocol the URL protocol to use (e.g. "jar" or "file" - without
	 *                 colon); also known as "scheme"
	 * @param location the location (e.g. the file path within that protocol); also
	 *                 known as "scheme-specific part"
	 * @param fragment the fragment within that location (e.g. anchor on an HTML
	 *                 page, as following after a "#" separator)
	 * @throws MalformedURLException if the given URL specification is not valid
	 * @see java.net.URI#URI(String, String, String)
	 */
	public UrlResource(@NonNull String protocol, @NonNull String location, String fragment)
			throws MalformedURLException {
		try {
			this.uri = new URI(protocol, location, fragment);
			this.url = this.uri.toURL();
		} catch (URISyntaxException ex) {
			MalformedURLException exToThrow = new MalformedURLException(ex.getMessage());
			exToThrow.initCause(ex);
			throw exToThrow;
		}
	}

	/**
	 * Determine a cleaned URL for the given original URL.
	 * 
	 * @param originalUrl  the original URL
	 * @param originalPath the original URL path
	 * @return the cleaned URL (possibly the original URL as-is)
	 * @see run.soeasy.framework.util.StringUtils#cleanPath
	 */
	private static URL getCleanedUrl(URL originalUrl, String originalPath) {
		String cleanedPath = StringUtils.cleanPath(originalPath);
		if (!cleanedPath.equals(originalPath)) {
			try {
				return new URL(cleanedPath);
			} catch (MalformedURLException ex) {
				// Cleaned URL path cannot be converted to URL -> take original URL.
			}
		}
		return originalUrl;
	}

	/**
	 * Lazily determine a cleaned URL for the given original URL.
	 * 
	 * @see #getCleanedUrl(URL, String)
	 */
	private URL getCleanedUrl() {
		URL cleanedUrl = this.cleanedUrl;
		if (cleanedUrl != null) {
			return cleanedUrl;
		}
		cleanedUrl = getCleanedUrl(this.url, (this.uri != null ? this.uri : this.url).toString());
		this.cleanedUrl = cleanedUrl;
		return cleanedUrl;
	}

	/**
	 * This implementation returns the underlying URL reference.
	 */
	@Override
	public URL getURL() {
		return this.url;
	}

	/**
	 * This implementation returns the underlying URI directly, if possible.
	 */
	@Override
	public URI getURI() throws IOException {
		return this.uri == null ? super.getURI() : this.uri;
	}

	/**
	 * This implementation creates a {@code UrlResource}, delegating to
	 * {@link #createRelativeURL(String)} for adapting the relative path.
	 * 
	 * @see #createRelativeURL(String)
	 */
	@Override
	public Resource createRelative(String relativePath) throws MalformedURLException {
		return new UrlResource(createRelativeURL(relativePath));
	}

	/**
	 * This delegate creates a {@code java.net.URL}, applying the given path
	 * relative to the path of the underlying URL of this resource descriptor. A
	 * leading slash will get dropped; a "#" symbol will get encoded.
	 * 
	 * @see #createRelative(String)
	 * @see java.net.URL#URL(java.net.URL, String)
	 */
	protected URL createRelativeURL(String relativePath) throws MalformedURLException {
		if (relativePath.startsWith("/")) {
			relativePath = relativePath.substring(1);
		}
		// # can appear in filenames, java.net.URL should not treat it as a fragment
		relativePath = StringUtils.replace(relativePath, "#", "%23");
		// Use the URL constructor for applying the relative path as a URL spec
		return new URL(this.url, relativePath);
	}

	/**
	 * This implementation returns the name of the file that this URL refers to.
	 * 
	 * @see java.net.URL#getPath()
	 */
	@Override
	public String getName() {
		return Resource.getName(getCleanedUrl().getPath());
	}

	/**
	 * This implementation returns a description that includes the URL.
	 */
	@Override
	public String getDescription() {
		return "URL [" + this.url + "]";
	}

	/**
	 * This implementation compares the underlying URL references.
	 */
	@Override
	public boolean equals(Object other) {
		return (this == other
				|| (other instanceof UrlResource && getCleanedUrl().equals(((UrlResource) other).getCleanedUrl())));
	}

	/**
	 * This implementation returns the hash code of the underlying URL reference.
	 */
	@Override
	public int hashCode() {
		return getCleanedUrl().hashCode();
	}

	protected URLConnection connect(boolean read) throws IOException {
		URLConnection urlConnection = url.openConnection();
		try {
			customizeConnection(urlConnection, read);
			urlConnection.connect();
		} catch (IOException e) {
			disconnect(urlConnection);
			throw e;
		}
		return urlConnection;
	}

	protected void customizeConnection(URLConnection con, boolean read) throws IOException {
		con.setUseCaches(con.getClass().getSimpleName().startsWith("JNLP"));
		if (con instanceof HttpURLConnection) {
			customizeHttpURLConnection((HttpURLConnection) con, read);
		}
	}

	protected void customizeHttpURLConnection(HttpURLConnection con, boolean read) throws IOException {
		con.setRequestMethod(read ? "GET" : "HEAD");
	}

	protected void disconnect(URLConnection urlConnection) throws IOException {
		if (urlConnection instanceof HttpURLConnection) {
			((HttpURLConnection) urlConnection).disconnect();
		}
	}

	@Override
	public long lastModified() throws IOException {
		boolean fileCheck = false;
		if (isFile()) {
			fileCheck = true;
			File file = getFile();
			if (file.exists()) {
				return file.lastModified();
			}
		}

		// Try a URL connection last-modified header
		URLConnection con = connect(false);
		try {
			con.connect();
			long lastModified = con.getLastModified();
			if (fileCheck && lastModified == 0 && con.getContentLengthLong() <= 0) {
				throw new FileNotFoundException(getDescription()
						+ " cannot be resolved in the file system for checking its last-modified timestamp");
			}
			return lastModified;
		} finally {
			disconnect(con);
		}
	}

	@Override
	public boolean exists() {
		if (isFile()) {
			try {
				return getFile().exists();
			} catch (IOException e) {
			}
		}
		try {
			// Try a URL connection content-length header
			URLConnection con = connect(false);
			try {
				HttpURLConnection httpCon = (con instanceof HttpURLConnection ? (HttpURLConnection) con : null);
				if (httpCon != null) {
					int code = httpCon.getResponseCode();
					if (code == HttpURLConnection.HTTP_OK) {
						return true;
					} else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
						return false;
					}
				}
				if (con.getContentLengthLong() > 0) {
					return true;
				}
				if (httpCon != null) {
					return false;
				} else {
					// Fall back to stream existence: can we open the stream?
					getInputStream().close();
					return true;
				}
			} finally {
				disconnect(con);
			}
		} catch (IOException ex) {
			return false;
		}
	}

	@Override
	public boolean isReadable() {
		return true;
	}

	/**
	 * This implementation opens an InputStream for the given URL.
	 * <p>
	 * It sets the {@code useCaches} flag to {@code false}, mainly to avoid jar file
	 * locking on Windows.
	 * 
	 * @see java.net.URL#openConnection()
	 * @see java.net.URLConnection#setUseCaches(boolean)
	 * @see java.net.URLConnection#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return new InternalInputStream();
	}
}
